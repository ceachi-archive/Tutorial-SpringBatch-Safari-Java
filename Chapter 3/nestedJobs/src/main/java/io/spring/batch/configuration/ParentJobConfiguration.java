/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.spring.batch.configuration;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.JobStepBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * @author Michael Minella
 */
@Configuration
public class ParentJobConfiguration {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	private Job childJob; // numele beanului din ChildJobConfiguration

	@Autowired // o luam din @EnableBatchProcessing, o sa ma folosesc de el in jobul pe care il creez
	private JobLauncher jobLauncher;

	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1")
				.tasklet((contribution, chunkContext) -> {
					System.out.println(">> This is step 1");
					return RepeatStatus.FINISHED;
				}).build();
	}
	
	/*
	 * 
	 * ca sa execut doar parentJob, trebuie sa intram in 
	 * application.properties si sa specificam explicit
	 * spring.batch.job.names=parentJob
	 */

	@Bean
	public Job parentJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		Step childJobStep = new JobStepBuilder(new StepBuilder("childJobStep")) // creem un step care sa execute un jobS
				.job(childJob) // pasez jobul meu care vreau sa executam
				.launcher(jobLauncher) // vrem sa punem joi jobRepository si transactionManager oferite de @EnableBatchProcessing
				.repository(jobRepository)
				.transactionManager(transactionManager)
				.build();
		// am creat un step care ar contine un job, si acum il punem in jobul nostru sub forma unui step
		return jobBuilderFactory.get("parentJob")
				.start(step1())
				.next(childJobStep)
				.build();
	}
}

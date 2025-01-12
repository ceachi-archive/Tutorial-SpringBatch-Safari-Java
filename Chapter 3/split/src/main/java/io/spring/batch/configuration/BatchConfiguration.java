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
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

/**
 * @author Michael Minella
 */
@Configuration
public class BatchConfiguration {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	
	// facem un bean, care este un Tasklet(interfata) a carei implementare e data de un obiect facut de noi
	@Bean
	public Tasklet tasklet() {
		return new CountingTasklet();
	}

	@Bean
	public Flow flow1() { // flow cu 1 singur step
		return new FlowBuilder<Flow>("flow1")
				.start(stepBuilderFactory.get("step1") // in loc sa facem o  metoda step1(), o definim direct aici 
						.tasklet(tasklet()).build()) // si ii dam ca tasklet, tasklet() facut de noi mai sus
				.build();
	}

	@Bean
	public Flow flow2() { // flow in care ii definim 2 stepuri
		return new FlowBuilder<Flow>("flow2")
				.start(stepBuilderFactory.get("step2")
						.tasklet(tasklet()).build())
				.next(stepBuilderFactory.get("step3")
						.tasklet(tasklet()).build())
				.build();
	}

	@Bean
	public Job job() {
		return jobBuilderFactory.get("job")// creem un job
				.start(flow1())// care sa executa flow-ul 1
				.split(new SimpleAsyncTaskExecutor()).add(flow2()) // dar adaugam sa execute in paralel si flow2
				.end()
				.build();
	}

	// Tasklet = interfata cu 1 singura metoda
	public static class CountingTasklet implements Tasklet {

		@Override
		public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
			// afisam contextul X a fost executat de thread-ul Y
			System.out.println(String.format("%s has been executed on thread %s", chunkContext.getStepContext().getStepName(), Thread.currentThread().getName()));
			return RepeatStatus.FINISHED;
		}
	}
}

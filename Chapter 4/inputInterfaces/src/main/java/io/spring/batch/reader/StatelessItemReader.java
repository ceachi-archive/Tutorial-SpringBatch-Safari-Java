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
package io.spring.batch.reader;

import java.util.Iterator;
import java.util.List;

import org.springframework.batch.item.ItemReader;

/**
 * @author Michael Minella
 */
public class StatelessItemReader implements ItemReader<String> {

	private final Iterator<String> data;

	public StatelessItemReader(List<String> data) {
		this.data = data.iterator();
	}

	@Override
	public String read() throws Exception {
		if(this.data.hasNext()) {
			return this.data.next();
		}
		else {
			//return "Never ending"; // poti da si asa, si ai sa vezi ca apeleaza read() dar nu se termina nicioadata
			return null;
		}
	}
}

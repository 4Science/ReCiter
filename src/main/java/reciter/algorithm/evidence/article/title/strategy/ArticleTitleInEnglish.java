/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package reciter.algorithm.evidence.article.title.strategy;

import java.util.List;

import reciter.algorithm.evidence.article.RemoveReCiterArticleStrategyContext;
import reciter.model.article.ReCiterArticle;
import reciter.model.identity.Identity;

/**
 * Discount matches when a publication is not in English.
 * https://github.com/wcmc-its/ReCiter/issues/103.
 * 
 * @author Jie
 *
 */
public class ArticleTitleInEnglish implements RemoveReCiterArticleStrategyContext {

	@Override
	public double executeStrategy(ReCiterArticle reCiterArticle, Identity identity) {
		String title = reCiterArticle.getArticleTitle();
		if (title != null && title.startsWith("[")) {
			reCiterArticle.setClusterInfo(reCiterArticle.getClusterInfo() + "[article title starts with '[']");
			reCiterArticle.setArticleTitleStartWithBracket(true);
			return 1;
		} else {
			reCiterArticle.setArticleTitleStartWithBracket(false);
			return 0;
		}
	}

	@Override
	public double executeStrategy(List<ReCiterArticle> reCiterArticles, Identity identity) {
		// TODO Auto-generated method stub
		return 0;
	}
}

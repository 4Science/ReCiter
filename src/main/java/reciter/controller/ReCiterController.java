package reciter.controller;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import reciter.Cwids;
import reciter.algorithm.util.ArticleTranslator;
import reciter.database.mongo.model.ESearchResult;
import reciter.database.mongo.model.GoldStandard;
import reciter.database.mongo.model.MeshTerm;
import reciter.engine.Engine;
import reciter.engine.EngineParameters;
import reciter.engine.ReCiterEngine;
import reciter.engine.erroranalysis.Analysis;
import reciter.model.article.ReCiterArticle;
import reciter.model.identity.Identity;
import reciter.model.pubmed.PubMedArticle;
import reciter.model.scopus.ScopusArticle;
import reciter.service.mongo.AnalysisService;
import reciter.service.mongo.ESearchResultService;
import reciter.service.mongo.GoldStandardService;
import reciter.service.mongo.IdentityService;
import reciter.service.mongo.MeshTermService;
import reciter.service.mongo.PubMedArticleFeatureService;
import reciter.service.mongo.PubMedService;
import reciter.service.mongo.ScopusService;
import reciter.xml.retriever.engine.ReCiterRetrievalEngine;

@Controller
public class ReCiterController {

	private static final Logger slf4jLogger = LoggerFactory.getLogger(ReCiterController.class);

	private final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	@Autowired
	private ESearchResultService eSearchResultService;

	@Autowired
	private PubMedService pubMedService;

	@Autowired
	private ReCiterRetrievalEngine aliasReCiterRetrievalEngine;

	@Autowired
	private IdentityService identityService;

	@Autowired
	private ScopusService scopusService;

	@Autowired
	private MeshTermService meshTermService;

	@Autowired
	private PubMedArticleFeatureService pubMedArticleFeatureService;

	@Autowired
	private GoldStandardService goldStandardService;

	@Autowired
	private AnalysisService analysisService;

	@RequestMapping(value = "/reciter/retrieve/articles/", method = RequestMethod.GET)
	@ResponseBody
	public void retrieveArticles() {
		long startTime = System.currentTimeMillis();
		slf4jLogger.info("Start time is: " + startTime);
		int i = 0;
		List<Identity> identities = new ArrayList<>();
		for (String cwid : Cwids.cwids) {
			slf4jLogger.info("Starting retrieval for : " + i + ", " + cwid);
			i++;
			Identity identity = identityService.findByCwid(cwid);
			identities.add(identity);
		}
		try {
			aliasReCiterRetrievalEngine.retrieve(identities);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long estimatedTime = System.currentTimeMillis() - startTime;
		slf4jLogger.info("elapsed time: " + estimatedTime);
	}
	
	@RequestMapping(value = "/reciter/retrieve/articles/by/cwid", method = RequestMethod.GET)
	@ResponseBody
	public void retrieveArticles(@RequestParam(value="cwid") String cwid) {
		long startTime = System.currentTimeMillis();
		slf4jLogger.info("Start time is: " + startTime);
		int i = 0;
		List<Identity> identities = new ArrayList<>();
		slf4jLogger.info("Starting retrieval for : " + i + ", " + cwid);
		Identity identity = identityService.findByCwid(cwid);
		identities.add(identity);
		try {
			aliasReCiterRetrievalEngine.retrieve(identities);
		} catch (IOException e) {
			e.printStackTrace();
		}
		long estimatedTime = System.currentTimeMillis() - startTime;
		slf4jLogger.info("elapsed time: " + estimatedTime);
	}

	@RequestMapping(value = "/reciter/all/analysis/", method = RequestMethod.GET)
	@ResponseBody
	public String runAllAnalysis() {
		for (String cwid : Cwids.cwids) {
			runAnalysis(cwid);
		}
		return "Success";
	}

	@RequestMapping(value = "/reciter/analysis/by/cwid", method = RequestMethod.GET)
	@ResponseBody
	public Analysis runAnalysis(@RequestParam(value="cwid") String cwid) {

		// find identity
		Identity identity = identityService.findByCwid(cwid);
		
		// find search results for this identity
		List<ESearchResult> eSearchResults = eSearchResultService.findByCwid(cwid);
		Set<Long> pmids = new HashSet<>();
		for (ESearchResult eSearchResult : eSearchResults) {
			pmids.addAll(eSearchResult.getESearchPmid().getPmids());
		}
		
		// create a list of pmids to pass to search
		List<Long> pmidList = new ArrayList<>(pmids);
		List<Long> filtered = new ArrayList<>();
		for (long pmid : pmidList) {
			if (pmid <= 27090613) {
				filtered.add(pmid);
			}
 		}
		
		List<PubMedArticle> pubMedArticles = pubMedService.findByPmids(filtered);
		List<ScopusArticle> scopusArticles = scopusService.findByPmids(filtered);
		
		// create temporary map to retrieve Scopus articles by PMID (at the stage below)
		Map<Long, ScopusArticle> map = new HashMap<>();
		for (ScopusArticle scopusArticle : scopusArticles) {
			map.put(scopusArticle.getPubmedId(), scopusArticle);
		}
		
		// combine PubMed and Scopus articles into a list of ReCiterArticle
		List<ReCiterArticle> reCiterArticles = new ArrayList<>();
		for (PubMedArticle pubMedArticle : pubMedArticles) {
			long pmid = pubMedArticle.getMedlineCitation().getMedlineCitationPMID().getPmid();
			if (map.containsKey(pmid)) {
				reCiterArticles.add(ArticleTranslator.translate(pubMedArticle, map.get(pmid)));
			} else {
				reCiterArticles.add(ArticleTranslator.translate(pubMedArticle, null));
			}
		}
		
		// calculate precision and recall
		EngineParameters parameters = new EngineParameters();
		parameters.setIdentity(identity);
		parameters.setPubMedArticles(pubMedArticles);
		parameters.setScopusArticles(Collections.emptyList());
		
		if (EngineParameters.getMeshCountMap() == null) {
			List<MeshTerm> meshTerms = meshTermService.findAll();
			slf4jLogger.info("Found " + meshTerms.size() + " mesh terms");
			Map<String, Long> meshCountMap = new HashMap<>();
			for (MeshTerm meshTerm : meshTerms) {
				meshCountMap.put(meshTerm.getMesh(), meshTerm.getCount());
			}
			EngineParameters.setMeshCountMap(meshCountMap);
		}

		GoldStandard goldStandard = goldStandardService.findByCwid(cwid);
		parameters.setKnownPmids(goldStandard.getKnownPmids());
		Engine engine = new ReCiterEngine();
		Analysis analysis = engine.run(parameters);

		slf4jLogger.info(analysis.toString());
		analysisService.save(analysis, cwid);
		return analysis;
	}
}
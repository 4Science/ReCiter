# ReCiter

ReCiter is a system for disambiguating author names in publication metadata. The algorithm is described in [Johnson et al. (2014)](https://www.ncbi.nlm.nih.gov/pubmed/24694772). The first functioning version of the algorithm was implemented by Steve Johnson in object-oriented perl, was designed to operate on raw Medline data, and wrote text files as output. This updated version of ReCiter is generalized to operate on data from PubMed, and (optionally) from Scopus. ReCiter uses the same core algorithm as the previous version, but with significant revisions and updates, including new strategies to improve recall by accounting for variations in names, as well as strategies that improve accuracy by leveraging new types of data for disambiguation.

This version of ReCiter is a Representational state transfer (RESTful) web service that communicates with a local database. It may be run on a regular basis to keep publication data accurate and up-to-date.

## Introduction

See the [ReCiter wiki](https://jl987-jie.gitbooks.io/reciter-wiki/content/) for an introduction to ReCiter.

## Getting Started

Instructions for getting started are in the [ReCiter wiki](https://jl987-jie.gitbooks.io/reciter-wiki/content/).

<!--1. Install [jdk 8](http://www.oracle.com/technetwork/java/javase/downloads/index-jsp-138363.html) or higher.
2. Import project into Eclipse, Intellij or your favorite IDE.
3. Clone the project into your local workspace by `git clone https://github.com/wcmc-its/ReCiter.git`.
4. Install the latest version of [MongoDB](https://www.mongodb.com/download-center).

More work needs to be done on the following:
5. Create a script to download data from PubMed and Scopus into MongoDB.
-->
## Getting Help

For help with ReCiter please email Jie Lin (jie265@gmail.com). You may expect a response within one to two business days. We use GitHub issues to track bugs and feature requests. If you find a bug, please contact Jie Lin or feel free to [open an issue](#opening-issues)

## Contributing

For more information about contributing, please contact Paul Albert (paa2013@med.cornell.edu) or Michael Bales (meb7002@med.cornell.edu).

## License

## Table of Fields
|                                                                                                                                                                                              | 
|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------| 
| Label,Description,Type                                                                                                                                                                       | 
| personIdentifier,institutionally-maintained identifier for a person,string                                                                                                                   | 
| dateAdded,date identity record added,date                                                                                                                                                    | 
| dateUpdated date identity record and/or suggested articles updated,,date                                                                                                                     | 
| accuracy,contains data on performance of algorithm,group of fields                                                                                                                           | 
| mode,"two options: testing and production; with testing, you're comparing against a gold standard; with production, you're using the gold standard to improve the quality of results",string | 
| overallAccuracy,average of precision and recall,float                                                                                                                                        | 
| precision,true positives / (false positives + true positives),float                                                                                                                          | 
| recall,true positives / (false negatives + true positives),float                                                                                                                             | 
| inGoldStandardButNotRetrieved,"PMIDs that are assserted in the gold standard not retrieved, e.g., because a researcher's surname changed or is not properly indexed",string                  | 
| countSuggestedArticles,number of articles that the ReCiter algorithm has suggested,integer                                                                                                   | 
| suggestedArticles,contains all the articles the ReCiter algorithm has suggested,group of fields                                                                                              | 
| article,individual article ReCiter has suggested,string                                                                                                                                      | 
| pmid,PubMed identifier of suggested article,string                                                                                                                                           | 
| score,unweighted count of pieces of evidence supporting ReCiter suggestion,string                                                                                                            | 
| userAssertion,"if 0, user did not assert authorship; if 1, user asserted authorship; if 2, user rejected authorship",integer                                                                 | 
| citation,an article ReCiter suggested,group of fields                                                                                                                                        | 
| pubDate,cover date article was published,group of fields                                                                                                                                     | 
| year,year article was published,string                                                                                                                                                       | 
| month,month article was published,string                                                                                                                                                     | 
| day,day article was published,string                                                                                                                                                         | 
| journal,publication venue of article,group of fields                                                                                                                                         | 
| journalTitleVerbose,verbose name of publication venue,string                                                                                                                                 | 
| journalTitleISOabbreviation,abbreviated name of publication venue,string                                                                                                                     | 
| articleTitle,title of article,string                                                                                                                                                         | 
| authorList,authors of article,group of fields                                                                                                                                                | 
| author,author of article,group of fields                                                                                                                                                     | 
| rank,order in which author appears,string                                                                                                                                                    | 
| lastName,last name of author,string                                                                                                                                                          | 
| firstName,first name of author,string                                                                                                                                                        | 
| initials,first and possibly middle initial of author,string                                                                                                                                  | 
| affiliation,"affiliation of author; includes PubMed and, if available, Scopus",group of fields                                                                                               | 
| afifliationPubMed,affiliation for particular author,string                                                                                                                                   | 
| affiliationScopus,affiliation label for author as recorded by Scopus,string                                                                                                                  | 
| affiliationIDScopus,affiliation ID for author as recorded by Scopus,string                                                                                                                   | 
| targetAuthor,"if ReCiter believes this author corresponds to the target author, then this is set to TRUE; else, FALSE",boolean                                                               | 
| volume,volume of article,string                                                                                                                                                              | 
| issue,issue of article,string                                                                                                                                                                | 
| pages,page range of article,string                                                                                                                                                           | 
| pmcid,PubMed Central identifier of article,string                                                                                                                                            | 
| doi,Digital Object Identifier of article; issues by publisher,string                                                                                                                         | 
| positiveEvidence,evidence ReCiter used to make a determination,group of fields                                                                                                               | 
| evidenceAffiliation,evidence that speaks to whether author has an affiliation consistent with institutionally maintained records,group of fields                                             | 
| institutionalAffiliation,locally maintained affiliation of target author,string                                                                                                              | 
| email,locally maintained email of target author,string                                                                                                                                       | 
| department,locally maintained department of target author,string                                                                                                                             | 
| articleAffiliation,affiliation as indexed in article,string                                                                                                                                  | 
| evidenceAuthorName,compares names of locally maintained author to name in bibliographic metadata,group of fields                                                                             | 
| evidenceInstitutionalAuthorName,name of author as recorded in bibliographic metadata,string                                                                                                  | 
| evidenceArticleAuthorName,locally maintained name of target author,string                                                                                                                    | 
| evidenceGrant,compares locally maintained grant identifiers to those indexed in bibliographic metadata,group of fields                                                                       | 
| grant,an individual grant in which there's a match between local data and bibliograhic metadata,string                                                                                       | 
| evidenceInstitutionGrant,grant identifier maintained by the institution,string                                                                                                               | 
| evidenceArticleGrant,grant identifier indexed in publication,string                                                                                                                          | 
| evidenceRelationship,names of locally known relationships of target author to names of authors in bibliographic metadata,group of fields                                                     | 
| relationship,individual examples of locally known relationships of target author to names of authors in bibliographic metadata,group of fields                                               | 
| relationshipName,name of person whose name matches one of the co-authors,string                                                                                                              | 
| relationshipType,"type of relationship (e.g., co-authorship, co-investigatorship, mentor/mentee, etc.)",string                                                                               | 
| evidenceYear,discrepancies between year of publication and year of degree,string                                                                                                             | 
| discrepancyDegreeYearBachelor,difference between year target author received a Bachelor's degree vs. year of publication,integer                                                             | 
| discrepancyDegreeYearTerminal,difference between year target author received a doctoral degree vs. year of publication,group of fields                                                       | 
| evidenceClustering,cases where article in question has been grouped in with other articles,group of fields                                                                                   | 
| meshMajorClustering,"cases where two articles shared the same Medical Subject Headers with the major designation, and were therefore grouped together",array of integers                     | 
| journalClustering,"cases where two articles shared the same Medical Subject Headers with the major designation, and were therefore grouped together",array of strings                        | 
| citesClustering,"cases where two articles shared the same journal, as tracked in PubMed Central, and were therefore grouped together",array of integers                                      | 
| bibliographicCouplingClustering,"cases where two articles were cited by the same article, as tracked in PubMed Central, and were therefore grouped together",array of integers               | 
| citedByClustering,"cases where two articles were cited by the same article, as tracked in PubMed Central, and were therefore grouped together",array of integers                             | 


/*
 Copyright (c) 2012, Regents of the University of Colorado
 All rights reserved.

 Redistribution and use in source and binary forms, with or without modification, 
 are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this 
    list of conditions and the following disclaimer.
   
 * Redistributions in binary form must reproduce the above copyright notice, 
    this list of conditions and the following disclaimer in the documentation 
    and/or other materials provided with the distribution.
   
 * Neither the name of the University of Colorado nor the names of its 
    contributors may be used to endorse or promote products derived from this 
    software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON 
 ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package abhi.medline;

import abhi.utils.StringUtils;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * The code in this class is complies with the 2012 NLM MedlineCitationSet DTD. Snippets of the DTD
 * have been copied and pasted into the comments of the classes below.
 * <p/>
 * DTD Key:<br>
 * * = 0 or more occurrences (optional element, repeatable)<br>
 * ? = 0 or 1 occurrences (optional element, at most 1)<br>
 * + = 1 or more occurrences (required element, repeatable)<br>
 * | = choice, one or the other but not both<br>
 * no symbol = required element<br>
 * <p/>
 * <pre>
 * <!ELEMENT    MedlineCitation (PMID, DateCreated, DateCompleted?, DateRevised?,
 *                               Article, MedlineJournalInfo, ChemicalList?,SupplMeshList?,
 *                               CitationSubset*, CommentsCorrectionsList?, GeneSymbolList?,
 *                               MeshHeadingList?,NumberOfReferences?, PersonalNameSubjectList?,
 *                               OtherID*, OtherAbstract*, KeywordList*, SpaceFlightMission*,
 *                               InvestigatorList?, GeneralNote*)>
 *
 * <!ATTLIST    MedlineCitation
 *                     Owner  (NLM | NASA | PIP | KIE | HSR | HMD | NOTNLM) "NLM"
 *                     Status (Completed | In-Process | PubMed-not-MEDLINE |
 *                             In-Data-Review | Publisher | MEDLINE |
 *                             OLDMEDLINE) #REQUIRED
 *                     VersionID CDATA #IMPLIED
 *                     VersionDate CDATA #IMPLIED>
 *
 * <!ELEMENT    PMID (#PCDATA)>
 * <!ATTLIST    PMID Version CDATA #REQUIRED>
 * <!ELEMENT	DateCompleted (Year,Month,Day)>
 * <!ELEMENT	DateCreated (Year,Month,Day)>
 * <!ELEMENT	DateRevised (Year,Month,Day)>
 * <!ELEMENT	Article (Journal,ArticleTitle,((Pagination, ELocationID*) |
 *                       ELocationID+),Abstract?, Affiliation?, AuthorList?,
 *                       Language+, DataBankList?, GrantList?,PublicationTypeList,
 *                       VernacularTitle?, ArticleDate*)>
 * <!ATTLIST	Article
 *                 PubModel (Print | Print-Electronic | Electronic |
 *                           Electronic-Print) #REQUIRED>
 * <!ELEMENT	MedlineJournalInfo (Country?, MedlineTA, NlmUniqueID?,ISSNLinking?)>
 * <!ELEMENT	ChemicalList (Chemical+)>
 * <!ELEMENT       SupplMeshList (SupplMeshName+)>
 * <!ELEMENT	CitationSubset (#PCDATA)>
 * <!ELEMENT	CommentsCorrectionsList (CommentsCorrections+)>
 * <!ELEMENT	GeneSymbolList (GeneSymbol+)>
 * <!ELEMENT	GeneSymbol (#PCDATA)>
 * <!ELEMENT	MeshHeadingList (MeshHeading+)>
 * <!ELEMENT	NumberOfReferences (#PCDATA)>
 * <!ELEMENT	PersonalNameSubjectList (PersonalNameSubject+)>
 * <!ELEMENT    OtherID (#PCDATA)>
 * <!ATTLIST    OtherID Source (NASA | KIE | PIP | POP | ARPL | CPC |
 *                              IND | CPFH | CLML | NRCBL | NLM) #REQUIRED>
 * <!ELEMENT	OtherAbstract (AbstractText+,CopyrightInformation?)>
 * <!ATTLIST	OtherAbstract Type (AAMC | AIDS | KIE | PIP |
 *                                  NASA | Publisher) #REQUIRED>
 * <!ELEMENT	KeywordList (Keyword+)>
 * <!ATTLIST	KeywordList Owner (NLM | NLM-AUTO | NASA | PIP | KIE | NOTNLM | HHS) "NLM">
 * <!ELEMENT	SpaceFlightMission (#PCDATA)>
 * <!ELEMENT	InvestigatorList (Investigator+)>
 * <!ELEMENT	GeneralNote (#PCDATA)>
 * <!ATTLIST	GeneralNote Owner (NLM | NASA | PIP | KIE | HSR | HMD) "NLM">
 * </pre>
 *
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 */
@Getter
@ToString
@XStreamAlias("MedlineCitation")
public class MedlineCitation {

    @XStreamAlias("Owner")
    @XStreamAsAttribute
    private String owner;

    @XStreamAlias("Status")
    @XStreamAsAttribute
    private String status;

    @XStreamAlias("PMID")
    private PubMedId pmid;

    @XStreamAlias("DateCreated")
    private Date dateCreated;

    @XStreamAlias("DateCompleted")
    private Date dateCompleted;

    @XStreamAlias("DateRevised")
    private Date dateRevised;

    @XStreamAlias("Article")
    private Article article;

    @XStreamAlias("MedlineJournalInfo")
    private MedlineJournalInfo medlineJournalInfo;

    @XStreamAlias("ChemicalList")
    private ChemicalList chemicalList;

    @XStreamImplicit(itemFieldName = "CitationSubset")
    private List<String> citationSubsets;

    @XStreamAlias("MeshHeadingList")
    private MeshHeadingList meshHeadingList;

    @XStreamAlias("CommentsCorrectionsList")
    private CommentsCorrectionsList commentsCorrectionsList;

    @XStreamAlias("NumberOfReferences")
    private Integer numberOfReferences;

    @XStreamImplicit
    private List<OtherId> otherIds;

    @XStreamImplicit(itemFieldName = "OtherAbstract")
    private List<Abstract> otherAbstractList;

    @XStreamImplicit
    private List<KeywordList> keywordListList;

    @XStreamImplicit
    private List<GeneralNote> generalNotes;

    @XStreamAlias("InvestigatorList")
    private InvestigatorList investigatorList;

    @XStreamAlias("PersonalNameSubjectList")
    private PersonalNameSubjectList personNameSubjectList;

    @XStreamImplicit(itemFieldName = "SpaceFlightMission")
    private List<String> spaceFlightMissions;

    @XStreamAlias("SupplMeshList")
    private SupplMeshList supplMeshList;

    @XStreamAlias("GeneSymbolList")
    private GeneSymbolList geneSymbolList;

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public PubMedId getPmid() {
        return pmid;
    }

    public void setPmid(PubMedId pmid) {
        this.pmid = pmid;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateCompleted() {
        return dateCompleted;
    }

    public void setDateCompleted(Date dateCompleted) {
        this.dateCompleted = dateCompleted;
    }

    public Date getDateRevised() {
        return dateRevised;
    }

    public void setDateRevised(Date dateRevised) {
        this.dateRevised = dateRevised;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public MedlineJournalInfo getMedlineJournalInfo() {
        return medlineJournalInfo;
    }

    public void setMedlineJournalInfo(MedlineJournalInfo medlineJournalInfo) {
        this.medlineJournalInfo = medlineJournalInfo;
    }

    public ChemicalList getChemicalList() {
        return chemicalList;
    }

    public void setChemicalList(ChemicalList chemicalList) {
        this.chemicalList = chemicalList;
    }

    public List<String> getCitationSubsets() {
        return citationSubsets;
    }

    public void setCitationSubsets(List<String> citationSubsets) {
        this.citationSubsets = citationSubsets;
    }

    public MeshHeadingList getMeshHeadingList() {
        return meshHeadingList;
    }

    public void setMeshHeadingList(MeshHeadingList meshHeadingList) {
        this.meshHeadingList = meshHeadingList;
    }

    public CommentsCorrectionsList getCommentsCorrectionsList() {
        return commentsCorrectionsList;
    }

    public void setCommentsCorrectionsList(CommentsCorrectionsList commentsCorrectionsList) {
        this.commentsCorrectionsList = commentsCorrectionsList;
    }

    public Integer getNumberOfReferences() {
        return numberOfReferences;
    }

    public void setNumberOfReferences(Integer numberOfReferences) {
        this.numberOfReferences = numberOfReferences;
    }

    public List<OtherId> getOtherIds() {
        return otherIds;
    }

    public void setOtherIds(List<OtherId> otherIds) {
        this.otherIds = otherIds;
    }

    public List<Abstract> getOtherAbstractList() {
        return otherAbstractList;
    }

    public void setOtherAbstractList(List<Abstract> otherAbstractList) {
        this.otherAbstractList = otherAbstractList;
    }

    public List<KeywordList> getKeywordListList() {
        return keywordListList;
    }

    public void setKeywordListList(List<KeywordList> keywordListList) {
        this.keywordListList = keywordListList;
    }

    public List<GeneralNote> getGeneralNotes() {
        return generalNotes;
    }

    public void setGeneralNotes(List<GeneralNote> generalNotes) {
        this.generalNotes = generalNotes;
    }

    public InvestigatorList getInvestigatorList() {
        return investigatorList;
    }

    public void setInvestigatorList(InvestigatorList investigatorList) {
        this.investigatorList = investigatorList;
    }

    public PersonalNameSubjectList getPersonNameSubjectList() {
        return personNameSubjectList;
    }

    public void setPersonNameSubjectList(PersonalNameSubjectList personNameSubjectList) {
        this.personNameSubjectList = personNameSubjectList;
    }

    public List<String> getSpaceFlightMissions() {
        return spaceFlightMissions;
    }

    public void setSpaceFlightMissions(List<String> spaceFlightMissions) {
        this.spaceFlightMissions = spaceFlightMissions;
    }

    public SupplMeshList getSupplMeshList() {
        return supplMeshList;
    }

    public void setSupplMeshList(SupplMeshList supplMeshList) {
        this.supplMeshList = supplMeshList;
    }

    public GeneSymbolList getGeneSymbolList() {
        return geneSymbolList;
    }

    public void setGeneSymbolList(GeneSymbolList geneSymbolList) {
        this.geneSymbolList = geneSymbolList;
    }

    /**
     * <pre>
     * <!ELEMENTPMID (#PCDATA)>
     * <!ATTLIST PMID Version CDATA #REQUIRED>
     * </pre>
     *
     * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
     */
    @EqualsAndHashCode
    @Setter
    @Getter
    @ToString
    @XStreamConverter(PubMedIdConverter.class)
    @XStreamAlias("PMID")
    public static class PubMedId {
        @XStreamAlias("Version")
        @XStreamAsAttribute
        private String version;

        private String pmid;

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getPmid() {
            return pmid;
        }

        public void setPmid(String pmid) {
            this.pmid = pmid;
        }
    }

    public static class PubMedIdConverter implements Converter {

        /*
         * (non-Javadoc)
         *
         * @see com.thoughtworks.xstream.converters.ConverterMatcher#canConvert(java.lang.Class)
         */
        @Override
        public boolean canConvert(Class clazz) {
            return PubMedId.class.equals(clazz);
        }

        /*
         * (non-Javadoc)
         *
         * @see com.thoughtworks.xstream.converters.Converter#marshal(java.lang.Object,
         * com.thoughtworks.xstream.io.HierarchicalStreamWriter,
         * com.thoughtworks.xstream.converters.MarshallingContext)
         */
        @Override
        public void marshal(Object object, HierarchicalStreamWriter hsw, MarshallingContext mc) {
            PubMedId pmid = (PubMedId) object;
            hsw.addAttribute("Version", pmid.getVersion());
            hsw.setValue(pmid.getPmid());
        }

        /*
         * (non-Javadoc)
         *
         * @see com.thoughtworks.xstream.converters.Converter#unmarshal(com.thoughtworks.xstream.io.
         * HierarchicalStreamReader, com.thoughtworks.xstream.converters.UnmarshallingContext)
         */
        @Override
        public Object unmarshal(HierarchicalStreamReader hsr, UnmarshallingContext umc) {
            PubMedId pmid = new PubMedId();
            pmid.setVersion(hsr.getAttribute("Version"));
            pmid.setPmid(hsr.getValue());
            return pmid;
        }

    }

    /**
     * <pre>
     * <!ELEMENT OtherID (#PCDATA)>
     * <!ATTLIST OtherID Source (NASA | KIE | PIP | POP | ARPL | CPC |
     *                           IND | CPFH | CLML | NRCBL | NLM) #REQUIRED>
     * </pre>
     *
     * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
     */
    @Setter
    @Getter
    @ToString
    @XStreamAlias("OtherID")
    @XStreamConverter(OtherIdConverter.class)
    public static class OtherId {
        @XStreamAlias("Source")
        @XStreamAsAttribute
        private String source;

        private String id;

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    public static class OtherIdConverter implements Converter {

        /*
         * (non-Javadoc)
         *
         * @see com.thoughtworks.xstream.converters.ConverterMatcher#canConvert(java.lang.Class)
         */
        public boolean canConvert(Class clazz) {
            return OtherId.class.equals(clazz);
        }

        /*
         * (non-Javadoc)
         *
         * @see com.thoughtworks.xstream.converters.Converter#marshal(java.lang.Object,
         * com.thoughtworks.xstream.io.HierarchicalStreamWriter,
         * com.thoughtworks.xstream.converters.MarshallingContext)
         */
        public void marshal(Object object, HierarchicalStreamWriter hsw, MarshallingContext mc) {
            OtherId id = (OtherId) object;
            hsw.addAttribute("Source", id.getSource());
            hsw.setValue(id.getId());
        }

        /*
         * (non-Javadoc)
         *
         * @see com.thoughtworks.xstream.converters.Converter#unmarshal(com.thoughtworks.xstream.io.
         * HierarchicalStreamReader, com.thoughtworks.xstream.converters.UnmarshallingContext)
         */
        public Object unmarshal(HierarchicalStreamReader hsr, UnmarshallingContext umc) {
            OtherId id = new OtherId();
            id.setSource(hsr.getAttribute("Source"));
            id.setId(hsr.getValue());
            return id;
        }

    }

    /**
     * <pre>
     * <!ELEMENT	Day (#PCDATA)>
     * <!ELEMENT	Month (#PCDATA)>
     * <!ELEMENT	Year (#PCDATA)>
     * </pre>
     *
     * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
     */
    @Getter
    public static class Date {
        @XStreamAlias("Year")
        private String year;
        @XStreamAlias("Month")
        private String month;
        @XStreamAlias("Day")
        private String day;

        @Override
        public String toString() {
            String dateStr = "";
            if (year != null)
                dateStr = year;
            if (month != null)
                dateStr = month + "/" + year;
            if (day != null)
                dateStr = month + "/" + day + "/" + year;
            return dateStr;
        }

        public String getYear() {
            return year;
        }

        public void setYear(String year) {
            this.year = year;
        }

        public String getMonth() {
            return month;
        }

        public void setMonth(String month) {
            this.month = month;
        }

        public String getDay() {
            return day;
        }

        public void setDay(String day) {
            this.day = day;
        }
    }

    /**
     * <pre>
     * <!ELEMENT	ArticleDate (Year,Month,Day)>
     * <!ATTLIST	ArticleDate DateType CDATA  #FIXED "Electronic">
     * </pre>
     *
     * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
     */
    @Getter
    @ToString
    public static class ArticleDate extends Date {
        @XStreamAlias("DateType")
        @XStreamAsAttribute
        private String dateType;
    }

    /**
     * <pre>
     * <!ELEMENT	PubDate ((Year, ((Month, Day?) | Season)?) | MedlineDate)>
     * <!ELEMENT	MedlineDate (#PCDATA)>
     * <!ELEMENT	Season (#PCDATA)>
     * </pre>
     *
     * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
     */
    @Getter
    public static class PubDate extends Date {
        @XStreamAlias("MedlineDate")
        private String medlineDate;

        @XStreamAlias("Season")
        private String season;

        /**
         * @return a string representation of the publication date. If there is a "medline date" then
         * it is returned. If not, then we check to see if there is a "year". If there is a
         * year, then we return a string formatted to "M/D/Y" depending on the availablity
         * of the month and day. If there is no year, then we check for a "season" returning
         * it if it exists. If none of the previous cases pass then we return null. I don't
         * think this method should ever return null however.
         */
        public String getDateStr() {

            if (getMedlineDate() != null && !getMedlineDate().isEmpty()) {
                return getMedlineDate();
            } else if (getYear() != null && !getYear().isEmpty()) {
                String dateStr = getYear();
                if (getDay() != null && !getDay().isEmpty()) {
                    dateStr = getDay() + "/" + dateStr;
                }
                if (getMonth() != null && !getMonth().isEmpty()) {
                    int month = -1;
                    if (getMonth().equalsIgnoreCase("Jan")) {
                        month = 1;
                    } else if (getMonth().equalsIgnoreCase("Feb")) {
                        month = 2;
                    } else if (getMonth().equalsIgnoreCase("Mar")) {
                        month = 3;
                    } else if (getMonth().equalsIgnoreCase("Apr")) {
                        month = 4;
                    } else if (getMonth().equalsIgnoreCase("May")) {
                        month = 5;
                    } else if (getMonth().equalsIgnoreCase("Jun")) {
                        month = 6;
                    } else if (getMonth().equalsIgnoreCase("Jul")) {
                        month = 7;
                    } else if (getMonth().equalsIgnoreCase("Aug")) {
                        month = 8;
                    } else if (getMonth().equalsIgnoreCase("Sep")) {
                        month = 9;
                    } else if (getMonth().equalsIgnoreCase("Oct")) {
                        month = 10;
                    } else if (getMonth().equalsIgnoreCase("Nov")) {
                        month = 11;
                    } else if (getMonth().equalsIgnoreCase("Dec")) {
                        month = 12;
                    } else {
                        throw new IllegalArgumentException("Unknown month: " + getMonth());
                    }
                    dateStr = month + "/" + dateStr;
                }
                return dateStr;
            } else if (getSeason() != null && !getSeason().isEmpty()) {
                return getSeason();
            } else {
                return null;
            }
        }

        @Override
        public String getYear(){
            if (getMedlineDate() != null && !getMedlineDate().isEmpty()) {
                String[] parts = getMedlineDate().split(" ")[0].split("-");
                return parts[0];
            }else{
                return super.getYear();
            }
        }

        public String getMedlineDate() {
            return medlineDate;
        }

        public void setMedlineDate(String medlineDate) {
            this.medlineDate = medlineDate;
        }

        public String getSeason() {
            return season;
        }

        public void setSeason(String season) {
            this.season = season;
        }
    }

    /**
     * <pre>
     * <!ELEMENT	Article (Journal,ArticleTitle,((Pagination, ELocationID*) |
     *                       ELocationID+),Abstract?, Affiliation?, AuthorList?,
     *                       Language+, DataBankList?, GrantList?,PublicationTypeList,
     *                       VernacularTitle?, ArticleDate*)>
     * <!ATTLIST	Article
     *                 PubModel (Print | Print-Electronic | Electronic |
     *                           Electronic-Print) #REQUIRED>
     *
     * <!ELEMENT	Journal (ISSN?, JournalIssue, Title?, ISOAbbreviation?)>
     * <!ELEMENT	ArticleTitle (#PCDATA)>
     * <!ELEMENT    Pagination ((StartPage, EndPage?, MedlinePgn?) | MedlinePgn)>
     * <!ELEMENT	ELocationID (#PCDATA)>
     * <!ATTLIST	ELocationID EIdType (doi | pii) #REQUIRED
     *              ValidYN  (Y | N) "Y">
     * <!ELEMENT	Abstract (AbstractText+,CopyrightInformation?)>
     * <!ELEMENT	Affiliation (#PCDATA)>
     * <!ELEMENT	AuthorList (Author+)>
     * <!ATTLIST	AuthorList CompleteYN (Y | N) "Y">
     * <!ELEMENT	Language (#PCDATA)>
     * <!ELEMENT	DataBankList (DataBank+)>
     * <!ATTLIST	DataBankList CompleteYN (Y | N) "Y">
     * <!ELEMENT	GrantList (Grant+)>
     * <!ATTLIST	GrantList CompleteYN (Y | N) "Y">
     * <!ELEMENT	PublicationTypeList (PublicationType+)>
     * <!ELEMENT	VernacularTitle (#PCDATA)>
     * <!ELEMENT	ArticleDate (Year,Month,Day)>
     * </pre>
     *
     * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
     */
    @Getter
    @ToString
    public static class Article {

        @XStreamAlias("PubModel")
        @XStreamAsAttribute
        private String pubModel;

        @XStreamAlias("Journal")
        private Journal journal;

        @XStreamAlias("ArticleTitle")
        private String articleTitle;

        @XStreamAlias("Pagination")
        private Pagination pagination;

        @XStreamAlias("Abstract")
        private Abstract theAbstract;

        @XStreamAlias("Affiliation")
        private String affiliation;

        @XStreamAlias("AuthorList")
        private AuthorList authorList;

        @XStreamImplicit(itemFieldName = "Language")
        private List<String> languages;

        @XStreamAlias("DataBankList")
        private DataBankList dataBankList;

        @XStreamAlias("PublicationTypeList")
        private PublicationTypeList publicationTypeList;

        @XStreamAlias("GrantList")
        private GrantList grantList;

        @XStreamAlias("ArticleDate")
        private ArticleDate articleDate;

        @XStreamAlias("VernacularTitle")
        private String vernacularTitle;

        @XStreamImplicit
        private List<ELocationId> eLocationIds;

        public String getPubModel() {
            return pubModel;
        }

        public void setPubModel(String pubModel) {
            this.pubModel = pubModel;
        }

        public Journal getJournal() {
            return journal;
        }

        public void setJournal(Journal journal) {
            this.journal = journal;
        }

        public String getArticleTitle() {
            return articleTitle;
        }

        public void setArticleTitle(String articleTitle) {
            this.articleTitle = articleTitle;
        }

        public Pagination getPagination() {
            return pagination;
        }

        public void setPagination(Pagination pagination) {
            this.pagination = pagination;
        }

        public Abstract getTheAbstract() {
            return theAbstract;
        }

        public void setTheAbstract(Abstract theAbstract) {
            this.theAbstract = theAbstract;
        }

        public String getAffiliation() {
            return affiliation;
        }

        public void setAffiliation(String affiliation) {
            this.affiliation = affiliation;
        }

        public AuthorList getAuthorList() {
            return authorList;
        }

        public void setAuthorList(AuthorList authorList) {
            this.authorList = authorList;
        }

        public List<String> getLanguages() {
            return languages;
        }

        public void setLanguages(List<String> languages) {
            this.languages = languages;
        }

        public DataBankList getDataBankList() {
            return dataBankList;
        }

        public void setDataBankList(DataBankList dataBankList) {
            this.dataBankList = dataBankList;
        }

        public PublicationTypeList getPublicationTypeList() {
            return publicationTypeList;
        }

        public void setPublicationTypeList(PublicationTypeList publicationTypeList) {
            this.publicationTypeList = publicationTypeList;
        }

        public GrantList getGrantList() {
            return grantList;
        }

        public void setGrantList(GrantList grantList) {
            this.grantList = grantList;
        }

        public ArticleDate getArticleDate() {
            return articleDate;
        }

        public void setArticleDate(ArticleDate articleDate) {
            this.articleDate = articleDate;
        }

        public String getVernacularTitle() {
            return vernacularTitle;
        }

        public void setVernacularTitle(String vernacularTitle) {
            this.vernacularTitle = vernacularTitle;
        }

        public List<ELocationId> geteLocationIds() {
            return eLocationIds;
        }

        public void seteLocationIds(List<ELocationId> eLocationIds) {
            this.eLocationIds = eLocationIds;
        }
    }

    /**
     * <pre>
     * <!ELEMENT	ELocationID (#PCDATA)>
     * <!ATTLIST	ELocationID EIdType (doi | pii) #REQUIRED
     *              ValidYN  (Y | N) "Y">
     * </pre>
     *
     * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
     */
    @Setter
    @Getter
    @ToString
    @XStreamAlias("ELocationID")
    @XStreamConverter(ELocationIdConverter.class)
    public static class ELocationId {
        @XStreamAlias("EIdType")
        @XStreamAsAttribute
        private String eIdType;

        @XStreamAlias("ValidYN")
        @XStreamAsAttribute
        private String validYn;

        private String eLocationId;

        public String geteIdType() {
            return eIdType;
        }

        public void seteIdType(String eIdType) {
            this.eIdType = eIdType;
        }

        public String getValidYn() {
            return validYn;
        }

        public void setValidYn(String validYn) {
            this.validYn = validYn;
        }

        public String geteLocationId() {
            return eLocationId;
        }

        public void seteLocationId(String eLocationId) {
            this.eLocationId = eLocationId;
        }
    }

    public static class ELocationIdConverter implements Converter {

        /*
         * (non-Javadoc)
         *
         * @see com.thoughtworks.xstream.converters.ConverterMatcher#canConvert(java.lang.Class)
         */
        @Override
        public boolean canConvert(Class clazz) {
            return ELocationId.class.equals(clazz);
        }

        /*
         * (non-Javadoc)
         *
         * @see com.thoughtworks.xstream.converters.Converter#marshal(java.lang.Object,
         * com.thoughtworks.xstream.io.HierarchicalStreamWriter,
         * com.thoughtworks.xstream.converters.MarshallingContext)
         */
        @Override
        public void marshal(Object object, HierarchicalStreamWriter hsw, MarshallingContext mc) {
            ELocationId id = (ELocationId) object;
            hsw.addAttribute("EIdType", id.geteIdType());
            hsw.addAttribute("ValidYN", id.getValidYn());
            hsw.setValue(id.geteLocationId());
        }

        /*
         * (non-Javadoc)
         *
         * @see com.thoughtworks.xstream.converters.Converter#unmarshal(com.thoughtworks.xstream.io.
         * HierarchicalStreamReader, com.thoughtworks.xstream.converters.UnmarshallingContext)
         */
        @Override
        public Object unmarshal(HierarchicalStreamReader hsr, UnmarshallingContext umc) {
            ELocationId id = new ELocationId();
            id.seteIdType(hsr.getAttribute("EIdType"));
            id.setValidYn(hsr.getAttribute("ValidYN"));
            id.seteLocationId(hsr.getValue());
            return id;
        }

    }

    /**
     * <pre>
     * <!ELEMENT	Journal (ISSN?, JournalIssue, Title?, ISOAbbreviation?)>
     * <!ELEMENT	ISSN (#PCDATA)>
     * <!ATTLIST	ISSN IssnType  (Electronic | Print) #REQUIRED>
     * <!ELEMENT	JournalIssue (Volume?, Issue?, PubDate)>
     * <!ATTLIST	JournalIssue CitedMedium (Internet | Print) #REQUIRED>
     * <!ELEMENT	Title (#PCDATA)>
     * <!ELEMENT	ISOAbbreviation (#PCDATA)>
     * </pre>
     *
     * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
     */
    @Getter
    @ToString
    public static class Journal {
        @XStreamAlias("ISSN")
        private Issn issn;

        @XStreamAlias("JournalIssue")
        private JournalIssue journalIssue;

        @XStreamAlias("Title")
        private String title;

        @XStreamAlias("ISOAbbreviation")
        private String isoAbbreviation;

        public Issn getIssn() {
            return issn;
        }

        public void setIssn(Issn issn) {
            this.issn = issn;
        }

        public JournalIssue getJournalIssue() {
            return journalIssue;
        }

        public void setJournalIssue(JournalIssue journalIssue) {
            this.journalIssue = journalIssue;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getIsoAbbreviation() {
            return isoAbbreviation;
        }

        public void setIsoAbbreviation(String isoAbbreviation) {
            this.isoAbbreviation = isoAbbreviation;
        }
    }

    /**
     * <pre>
     * <!ELEMENT	ISSN (#PCDATA)>
     * <!ATTLIST	ISSN IssnType  (Electronic | Print) #REQUIRED>
     * </pre>
     *
     * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
     */
    @Setter
    @Getter
    @ToString
    @XStreamConverter(IssnConverter.class)
    public static class Issn {
        @XStreamAlias("IssnType")
        @XStreamAsAttribute
        private String type;

        private String issn;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getIssn() {
            return issn;
        }

        public void setIssn(String issn) {
            this.issn = issn;
        }
    }

    public static class IssnConverter implements Converter {

        /*
         * (non-Javadoc)
         *
         * @see com.thoughtworks.xstream.converters.ConverterMatcher#canConvert(java.lang.Class)
         */
        @Override
        public boolean canConvert(Class clazz) {
            return Issn.class.equals(clazz);
        }

        /*
         * (non-Javadoc)
         *
         * @see com.thoughtworks.xstream.converters.Converter#marshal(java.lang.Object,
         * com.thoughtworks.xstream.io.HierarchicalStreamWriter,
         * com.thoughtworks.xstream.converters.MarshallingContext)
         */
        @Override
        public void marshal(Object object, HierarchicalStreamWriter hsw, MarshallingContext mc) {
            Issn issn = (Issn) object;
            hsw.addAttribute("IssnType", issn.getType());
            hsw.setValue(issn.getIssn());
        }

        /*
         * (non-Javadoc)
         *
         * @see com.thoughtworks.xstream.converters.Converter#unmarshal(com.thoughtworks.xstream.io.
         * HierarchicalStreamReader, com.thoughtworks.xstream.converters.UnmarshallingContext)
         */
        @Override
        public Object unmarshal(HierarchicalStreamReader hsr, UnmarshallingContext umc) {
            Issn issn = new Issn();
            issn.setType(hsr.getAttribute("IssnType"));
            issn.setIssn(hsr.getValue());
            return issn;
        }

    }

    /**
     * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
     *         <p/>
     *         <pre>
     *         <!ELEMENT	JournalIssue (Volume?, Issue?, PubDate)>
     *         <!ATTLIST	JournalIssue CitedMedium (Internet | Print) #REQUIRED>
     *         <!ELEMENT	Volume (#PCDATA)>
     *         <!ELEMENT	Issue (#PCDATA)>
     *         <!ELEMENT	PubDate ((Year, ((Month, Day?) | Season)?) | MedlineDate)>
     *         </pre>
     */
    @Getter
    @ToString
    public static class JournalIssue {
        @XStreamAlias("CitedMedium")
        @XStreamAsAttribute
        private String citedMedium;

        @XStreamAlias("Volume")
        private String volume;
        @XStreamAlias("Issue")
        private String issue;
        @XStreamAlias("PubDate")
        private PubDate pubDate;

        public String getCitedMedium() {
            return citedMedium;
        }

        public void setCitedMedium(String citedMedium) {
            this.citedMedium = citedMedium;
        }

        public String getVolume() {
            return volume;
        }

        public void setVolume(String volume) {
            this.volume = volume;
        }

        public String getIssue() {
            return issue;
        }

        public void setIssue(String issue) {
            this.issue = issue;
        }

        public PubDate getPubDate() {
            return pubDate;
        }

        public void setPubDate(PubDate pubDate) {
            this.pubDate = pubDate;
        }
    }

    /**
     * NOTE: StartPage and EndPage in the Pagination element are not currently used; are reserved
     * for future use.
     * <p/>
     * <pre>
     * <!ELEMENT   Pagination ((StartPage, EndPage?, MedlinePgn?) | MedlinePgn)>
     * <!ELEMENT   StartPage (#PCDATA)>
     * <!ELEMENT   EndPage (#PCDATA)>
     * <!ELEMENT   MedlinePgn (#PCDATA)>
     * </pre>
     *
     * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
     */
    @Getter
    public static class Pagination {

        @XStreamAlias("MedlinePgn")
        private String medlinePagination;

        @XStreamAlias("StartPage")
        private String startPage;

        @XStreamAlias("EndPage")
        private String endPage;

        @Override
        public String toString() {
            if (medlinePagination != null)
                return medlinePagination;
            if (startPage != null && endPage != null)
                return startPage + "-" + endPage;
            return null;
        }

    }

    /**
     * <pre>
     * <!ELEMENT	Abstract (AbstractText+,CopyrightInformation?)>
     * <!ELEMENT	CopyrightInformation (#PCDATA)>
     * </pre>
     *
     * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
     */
    @Getter
    public static class Abstract {
        @XStreamAlias("Type")
        @XStreamAsAttribute
        private String type;

        @XStreamAlias("AbstractText")
        @XStreamImplicit
        private List<AbstractText> abstractTexts;

        @XStreamAlias("CopyrightInformation")
        private String copyrightInformation;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public List<AbstractText> getAbstractTexts() {
            return abstractTexts;
        }

        public void setAbstractTexts(List<AbstractText> abstractTexts) {
            this.abstractTexts = abstractTexts;
        }

        public String getCopyrightInformation() {
            return copyrightInformation;
        }

        public void setCopyrightInformation(String copyrightInformation) {
            this.copyrightInformation = copyrightInformation;
        }

        @Override
        public String toString() {
            StringBuffer sb = new StringBuffer();
            for (AbstractText text : abstractTexts) {
                sb.append(text.getAbstractText() + " ");
            }
            return StringUtils.removeLastCharacter(sb.toString());
        }
    }

    /**
     * <pre>
     * <!ELEMENT	AbstractText (#PCDATA)>
     * <!ATTLIST       AbstractText
     *                 Label CDATA #IMPLIED
     *                 NlmCategory (UNLABELLED | BACKGROUND | OBJECTIVE | METHODS |
     *                              RESULTS | CONCLUSIONS) #IMPLIED>
     * </pre>
     *
     * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
     */
    @Setter
    @Getter
    @ToString
    @XStreamConverter(AbstractTextConverter.class)
    public static class AbstractText {
        @XStreamAlias("Label")
        @XStreamAsAttribute
        private String label;

        @XStreamAlias("NlmCategory")
        @XStreamAsAttribute
        private String nlmCategory;

        private String abstractText;

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getNlmCategory() {
            return nlmCategory;
        }

        public void setNlmCategory(String nlmCategory) {
            this.nlmCategory = nlmCategory;
        }

        public String getAbstractText() {
            return abstractText;
        }

        public void setAbstractText(String abstractText) {
            this.abstractText = abstractText;
        }
    }

    public static class AbstractTextConverter implements Converter {

        /*
         * (non-Javadoc)
         *
         * @see com.thoughtworks.xstream.converters.ConverterMatcher#canConvert(java.lang.Class)
         */
        @Override
        public boolean canConvert(Class clazz) {
            return AbstractText.class.equals(clazz);
        }

        /*
         * (non-Javadoc)
         *
         * @see com.thoughtworks.xstream.converters.Converter#marshal(java.lang.Object,
         * com.thoughtworks.xstream.io.HierarchicalStreamWriter,
         * com.thoughtworks.xstream.converters.MarshallingContext)
         */
        @Override
        public void marshal(Object object, HierarchicalStreamWriter hsw, MarshallingContext mc) {
            AbstractText abstractText = (AbstractText) object;
            hsw.addAttribute("Label", abstractText.getLabel());
            hsw.addAttribute("NlmCategory", abstractText.getNlmCategory());
            hsw.setValue(abstractText.getAbstractText());
        }

        /*
         * (non-Javadoc)
         *
         * @see com.thoughtworks.xstream.converters.Converter#unmarshal(com.thoughtworks.xstream.io.
         * HierarchicalStreamReader, com.thoughtworks.xstream.converters.UnmarshallingContext)
         */
        @Override
        public Object unmarshal(HierarchicalStreamReader hsr, UnmarshallingContext umc) {
            AbstractText abstractText = new AbstractText();
            abstractText.setLabel(hsr.getAttribute("Label"));
            abstractText.setLabel(hsr.getAttribute("NlmCategory"));
            abstractText.setAbstractText(hsr.getValue());
            return abstractText;
        }

    }

    /**
     * <pre>
     * <!ELEMENT	AuthorList (Author+)>
     * <!ATTLIST	AuthorList CompleteYN (Y | N) "Y">
     * </pre>
     *
     * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
     */
    @Getter
    @ToString
    public static class AuthorList {
        @XStreamImplicit
        private List<Author> authors;

        public List<Author> getAuthors() {
            return authors;
        }

        public void setAuthors(List<Author> authors) {
            this.authors = authors;
        }
    }

    /**
     * <pre>
     * <!ELEMENT	Author (((LastName, ForeName?, Initials?, Suffix?) |
     *                          CollectiveName),NameID*)>
     * <!ATTLIST	Author ValidYN (Y | N) "Y">
     * <!ELEMENT	LastName (#PCDATA)>
     * <!ELEMENT	ForeName (#PCDATA)>
     * <!ELEMENT	Initials (#PCDATA)>
     * <!ELEMENT	Suffix (#PCDATA)>
     * <!ELEMENT	CollectiveName (#PCDATA)>
     * <!ELEMENT    NameID (#PCDATA)>
     * </pre>
     *
     * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
     */
    @Getter
    @ToString
    @XStreamAlias("Author")
    public static class Author {
        @XStreamAlias("ValidYN")
        @XStreamAsAttribute
        private String validYn;

        @XStreamAlias("LastName")
        private String lastName;

        @XStreamAlias("ForeName")
        private String foreName;

        @XStreamAlias("Initials")
        private String initials;

        @XStreamAlias("Suffix")
        private String suffix;

        @XStreamAlias("CollectiveName")
        private String collectiveName;

        @XStreamAlias("NameID")
        private NameId nameId;

        @XStreamAlias("AffiliationInfo")
        private String affiliationInfo;

        @XStreamAlias("Identifier")
        private String identifier;

        public String getValidYn() {
            return validYn;
        }

        public void setValidYn(String validYn) {
            this.validYn = validYn;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getForeName() {
            return foreName;
        }

        public void setForeName(String foreName) {
            this.foreName = foreName;
        }

        public String getInitials() {
            return initials;
        }

        public void setInitials(String initials) {
            this.initials = initials;
        }

        public String getSuffix() {
            return suffix;
        }

        public void setSuffix(String suffix) {
            this.suffix = suffix;
        }

        public String getCollectiveName() {
            return collectiveName;
        }

        public void setCollectiveName(String collectiveName) {
            this.collectiveName = collectiveName;
        }

        public NameId getNameId() {
            return nameId;
        }

        public void setNameId(NameId nameId) {
            this.nameId = nameId;
        }

        public String getAffiliationInfo() {
            return affiliationInfo;
        }

        public void setAffiliationInfo(String affiliationInfo) {
            this.affiliationInfo = affiliationInfo;
        }

        public String getIdentifier() {
            return identifier;
        }

        public void setIdentifier(String identifier) {
            this.identifier = identifier;
        }
    }

    /**
     * <pre>
     * <!ELEMENT	InvestigatorList (Investigator+)>
     * </pre>
     *
     * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
     */
    @Getter
    @ToString
    public static class InvestigatorList {
        @XStreamImplicit
        private List<Investigator> investigators;
    }

    /**
     * NOTE: NameID in Investigator element is not currently used; reserved for future use.
     * <p/>
     * <pre>
     * <!ELEMENT	Investigator (LastName,ForeName?, Initials?,Suffix?,NameID*,Affiliation?)>
     * <!ATTLIST	Investigator ValidYN (Y | N) "Y">
     * <!ELEMENT	LastName (#PCDATA)>
     * <!ELEMENT	ForeName (#PCDATA)>
     * <!ELEMENT	Initials (#PCDATA)>
     * <!ELEMENT	Suffix (#PCDATA)>
     * <!ELEMENT    NameID (#PCDATA)>
     * <!ELEMENT	Affiliation (#PCDATA)>
     * </pre>
     *
     * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
     */
    @Getter
    @ToString
    @XStreamAlias("Investigator")
    public static class Investigator {
        @XStreamAlias("ValidYN")
        @XStreamAsAttribute
        private String validYn;

        @XStreamAlias("LastName")
        private String lastName;

        @XStreamAlias("ForeName")
        private String foreName;

        @XStreamAlias("Initials")
        private String initials;

        @XStreamAlias("Suffix")
        private String suffix;

        @XStreamAlias("Affiliation")
        private String affiliation;

        @XStreamAlias("AffiliationInfo")
        private String affiliationInfo;

        @XStreamAlias("NameID")
        private NameId nameId;

        public String getValidYn() {
            return validYn;
        }

        public void setValidYn(String validYn) {
            this.validYn = validYn;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getForeName() {
            return foreName;
        }

        public void setForeName(String foreName) {
            this.foreName = foreName;
        }

        public String getInitials() {
            return initials;
        }

        public void setInitials(String initials) {
            this.initials = initials;
        }

        public String getSuffix() {
            return suffix;
        }

        public void setSuffix(String suffix) {
            this.suffix = suffix;
        }

        public String getAffiliation() {
            return affiliation;
        }

        public void setAffiliation(String affiliation) {
            this.affiliation = affiliation;
        }

        public NameId getNameId() {
            return nameId;
        }

        public void setNameId(NameId nameId) {
            this.nameId = nameId;
        }

        public String getAffiliationInfo() {
            return affiliationInfo;
        }

        public void setAffiliationInfo(String affiliationInfo) {
            this.affiliationInfo = affiliationInfo;
        }
    }

    /**
     * <pre>
     * <!ELEMENT   NameID (#PCDATA)>
     * <!ATTLIST   NameID
     *             Source CDATA #REQUIRED >
     * </pre>
     *
     * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
     */
    @Setter
    @Getter
    @ToString
    @XStreamConverter(NameIdConverter.class)
    public static class NameId {
        @XStreamAlias("Source")
        @XStreamAsAttribute
        private String source;

        private String nameId;

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getNameId() {
            return nameId;
        }

        public void setNameId(String nameId) {
            this.nameId = nameId;
        }
    }

    public static class NameIdConverter implements Converter {

        /*
         * (non-Javadoc)
         *
         * @see com.thoughtworks.xstream.converters.ConverterMatcher#canConvert(java.lang.Class)
         */
        @Override
        public boolean canConvert(Class clazz) {
            return NameId.class.equals(clazz);
        }

        /*
         * (non-Javadoc)
         *
         * @see com.thoughtworks.xstream.converters.Converter#marshal(java.lang.Object,
         * com.thoughtworks.xstream.io.HierarchicalStreamWriter,
         * com.thoughtworks.xstream.converters.MarshallingContext)
         */
        @Override
        public void marshal(Object object, HierarchicalStreamWriter hsw, MarshallingContext mc) {
            NameId nameId = (NameId) object;
            hsw.addAttribute("Source", nameId.getSource());
            hsw.setValue(nameId.getNameId());
        }

        /*
         * (non-Javadoc)
         *
         * @see com.thoughtworks.xstream.converters.Converter#unmarshal(com.thoughtworks.xstream.io.
         * HierarchicalStreamReader, com.thoughtworks.xstream.converters.UnmarshallingContext)
         */
        @Override
        public Object unmarshal(HierarchicalStreamReader hsr, UnmarshallingContext umc) {
            NameId nameId = new NameId();
            nameId.setSource(hsr.getAttribute("Source"));
            nameId.setNameId(hsr.getValue());
            return nameId;
        }
    }

    /**
     * <pre>
     * <!ELEMENT	PersonalNameSubjectList (PersonalNameSubject+)>
     * </pre>
     *
     * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
     */
    @Getter
    @ToString
    public static class PersonalNameSubjectList {
        @XStreamImplicit
        private List<PersonalNameSubject> personalNameSubjects;
    }

    /**
     * <pre>
     * <!ELEMENT	PersonalNameSubject (LastName,ForeName?, Initials?,Suffix?)>
     * <!ELEMENT	LastName (#PCDATA)>
     * <!ELEMENT	ForeName (#PCDATA)>
     * <!ELEMENT	Initials (#PCDATA)>
     * <!ELEMENT	Suffix (#PCDATA)>
     * </pre>
     *
     * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
     */
    @Getter
    @ToString
    @XStreamAlias("PersonalNameSubject")
    public static class PersonalNameSubject {
        @XStreamAlias("LastName")
        private String lastName;

        @XStreamAlias("ForeName")
        private String foreName;

        @XStreamAlias("Initials")
        private String initials;

        @XStreamAlias("Suffix")
        private String suffix;

    }

    /**
     * <pre>
     * <!ELEMENT       SupplMeshList (SupplMeshName+)>
     * </pre>
     *
     * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
     */
    @Getter
    @ToString
    public static class SupplMeshList {
        @XStreamImplicit
        private List<SupplMeshName> supplMeshNames;
    }

    /**
     * <pre>
     * <!ELEMENT       SupplMeshName (#PCDATA)>
     * <!ATTLIST       SupplMeshName Type (Disease | Protocol) #REQUIRED>
     * </pre>
     *
     * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
     */
    @Setter
    @Getter
    @ToString
    @XStreamAlias("SupplMeshName")
    @XStreamConverter(SupplMeshNameConverter.class)
    public static class SupplMeshName {
        @XStreamAlias("Type")
        @XStreamAsAttribute
        private String type;

        private String name;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class SupplMeshNameConverter implements Converter {

        /*
         * (non-Javadoc)
         *
         * @see com.thoughtworks.xstream.converters.ConverterMatcher#canConvert(java.lang.Class)
         */
        @Override
        public boolean canConvert(Class clazz) {
            return SupplMeshName.class.equals(clazz);
        }

        /*
         * (non-Javadoc)
         *
         * @see com.thoughtworks.xstream.converters.Converter#marshal(java.lang.Object,
         * com.thoughtworks.xstream.io.HierarchicalStreamWriter,
         * com.thoughtworks.xstream.converters.MarshallingContext)
         */
        @Override
        public void marshal(Object object, HierarchicalStreamWriter hsw, MarshallingContext mc) {
            SupplMeshName name = (SupplMeshName) object;
            hsw.addAttribute("Type", name.getType());
            hsw.setValue(name.getName());
        }

        /*
         * (non-Javadoc)
         *
         * @see com.thoughtworks.xstream.converters.Converter#unmarshal(com.thoughtworks.xstream.io.
         * HierarchicalStreamReader, com.thoughtworks.xstream.converters.UnmarshallingContext)
         */
        @Override
        public Object unmarshal(HierarchicalStreamReader hsr, UnmarshallingContext umc) {
            SupplMeshName name = new SupplMeshName();
            name.setType(hsr.getAttribute("Type"));
            name.setName(hsr.getValue());
            return name;
        }

    }

    /**
     * <pre>
     * <!ELEMENT	DataBankList (DataBank+)>
     * <!ATTLIST	DataBankList CompleteYN (Y | N) "Y">
     * </pre>
     *
     * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
     */
    @Getter
    @ToString
    public static class DataBankList {
        @XStreamAlias("CompleteYN")
        @XStreamAsAttribute
        private String completeYn;

        @XStreamImplicit
        private List<DataBank> dataBanks;
    }

    /**
     * <pre>
     * <!ELEMENT	DataBank (DataBankName, AccessionNumberList?)>
     * <!ELEMENT	DataBankName (#PCDATA)>
     * </pre>
     *
     * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
     */
    @Getter
    @ToString
    @XStreamAlias("DataBank")
    public static class DataBank {

        @XStreamAlias("DataBankName")
        private String dataBankName;

        @XStreamAlias("AccessionNumberList")
        private AccessionNumberList accessionNumberList;
    }

    /**
     * <pre>
     * <!ELEMENT	GrantList (Grant+)>
     * <!ATTLIST	GrantList CompleteYN (Y | N) "Y">
     * </pre>
     *
     * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
     */
    @Getter
    @ToString
    public static class GrantList {
        @XStreamAlias("CompleteYN")
        @XStreamAsAttribute
        private String completeYn;

        @XStreamImplicit
        private List<Grant> grants;
    }

    /**
     * <pre>
     * <!ELEMENT	Grant (GrantID?, Acronym?, Agency, Country)>
     * <!ELEMENT	GrantID (#PCDATA)>
     * <!ELEMENT	Acronym (#PCDATA)>
     * <!ELEMENT	Agency (#PCDATA)>
     * <!ELEMENT	Country (#PCDATA)>
     * </pre>
     *
     * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
     */
    @Getter
    @ToString
    @XStreamAlias("Grant")
    public static class Grant {

        @XStreamAlias("GrantID")
        private String grantId;

        @XStreamAlias("Acronym")
        private String acronym;

        @XStreamAlias("Agency")
        private String agency;

        @XStreamAlias("Country")
        private String country;
    }

    /**
     * <pre>
     * <!ELEMENT	AccessionNumberList (AccessionNumber+)>
     * <!ELEMENT	AccessionNumber (#PCDATA)>
     * </pre>
     *
     * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
     */
    @Getter
    @ToString
    public static class AccessionNumberList {
        @XStreamImplicit(itemFieldName = "AccessionNumber")
        private List<String> accessionNumbers;
    }

    /**
     * <pre>
     * <!ELEMENT	PublicationTypeList (PublicationType+)>
     * <!ELEMENT	PublicationType (#PCDATA)>
     * </pre>
     *
     * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
     */
    @Getter
    @ToString
    public static class PublicationTypeList {
        @XStreamImplicit(itemFieldName = "PublicationType")
        private List<String> publicationTypes;
    }

    /**
     * <pre>
     * <!ELEMENT	MedlineJournalInfo (Country?, MedlineTA, NlmUniqueID?,ISSNLinking?)>
     * <!ELEMENT	Country (#PCDATA)>
     * <!ELEMENT	MedlineTA (#PCDATA)>
     * <!ELEMENT	NlmUniqueID (#PCDATA)>
     * <!ELEMENT    ISSNLinking (#PCDATA)>
     * </pre>
     *
     * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
     */
    @Getter
    @ToString
    public static class MedlineJournalInfo {
        @XStreamAlias("Country")
        private String country;

        @XStreamAlias("MedlineTA")
        private String medlineTa;

        @XStreamAlias("NlmUniqueID")
        private String nlmUniqueId;

        @XStreamAlias("ISSNLinking")
        private String issnLinking;

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getMedlineTa() {
            return medlineTa;
        }

        public void setMedlineTa(String medlineTa) {
            this.medlineTa = medlineTa;
        }

        public String getNlmUniqueId() {
            return nlmUniqueId;
        }

        public void setNlmUniqueId(String nlmUniqueId) {
            this.nlmUniqueId = nlmUniqueId;
        }

        public String getIssnLinking() {
            return issnLinking;
        }

        public void setIssnLinking(String issnLinking) {
            this.issnLinking = issnLinking;
        }
    }

    /**
     * <pre>
     * <!ELEMENT	ChemicalList (Chemical+)>
     * </pre>
     *
     * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
     */
    @Getter
    @ToString
    public static class ChemicalList {
        @XStreamImplicit
        private List<Chemical> chemicals;
    }

    /**
     * <pre>
     * <!ELEMENT	Chemical (RegistryNumber,NameOfSubstance)>
     * <!ELEMENT	NameOfSubstance (#PCDATA)>
     * <!ELEMENT	RegistryNumber (#PCDATA)>
     * </pre>
     *
     * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
     */
    @Getter
    @ToString
    @XStreamAlias("Chemical")
    public static class Chemical {
        @XStreamAlias("RegistryNumber")
        private String registryNumber;

        @XStreamAlias("NameOfSubstance")
        private String substanceName;
    }

    /**
     * <pre>
     * <!ELEMENT	MeshHeadingList (MeshHeading+)>
     * </pre>
     *
     * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
     */
    @Getter
    @ToString
    public static class MeshHeadingList {
        @XStreamImplicit
        private List<MeshHeading> meshHeadings;

        public List<MeshHeading> getMeshHeadings() {
            return meshHeadings;
        }

        public void setMeshHeadings(List<MeshHeading> meshHeadings) {
            this.meshHeadings = meshHeadings;
        }
    }

    /**
     * <pre>
     * <!ELEMENT	MeshHeading (DescriptorName, QualifierName*)>
     * </pre>
     *
     * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
     */
    @Getter
    @ToString
    @XStreamAlias("MeshHeading")
    public static class MeshHeading {

        @XStreamAlias("DescriptorName")
        private MeshDescriptorName descriptorName;

        @XStreamImplicit
        private List<MeshQualifierName> qualifierNames;

        public MeshDescriptorName getDescriptorName() {
            return descriptorName;
        }

        public void setDescriptorName(MeshDescriptorName descriptorName) {
            this.descriptorName = descriptorName;
        }

        public List<MeshQualifierName> getQualifierNames() {
            return qualifierNames;
        }

        public void setQualifierNames(List<MeshQualifierName> qualifierNames) {
            this.qualifierNames = qualifierNames;
        }
    }

    /**
     * <pre>
     * <!ELEMENT	QualifierName (#PCDATA)>
     * <!ATTLIST	QualifierName MajorTopicYN (Y | N) "N">
     * </pre>
     *
     * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
     */
    @Setter
    @Getter
    @ToString
    @XStreamAlias("QualifierName")
    @XStreamConverter(MeshQualiferNameConverter.class)
    public static class MeshQualifierName {
        @XStreamAlias("MajorTopicYN")
        @XStreamAsAttribute
        private String majorTopicYn;

        private String name;

        public String getMajorTopicYn() {
            return majorTopicYn;
        }

        public void setMajorTopicYn(String majorTopicYn) {
            this.majorTopicYn = majorTopicYn;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class MeshQualiferNameConverter implements Converter {

        /*
         * (non-Javadoc)
         *
         * @see com.thoughtworks.xstream.converters.ConverterMatcher#canConvert(java.lang.Class)
         */
        @Override
        public boolean canConvert(Class clazz) {
            return MeshQualifierName.class.equals(clazz);
        }

        /*
         * (non-Javadoc)
         *
         * @see com.thoughtworks.xstream.converters.Converter#marshal(java.lang.Object,
         * com.thoughtworks.xstream.io.HierarchicalStreamWriter,
         * com.thoughtworks.xstream.converters.MarshallingContext)
         */
        @Override
        public void marshal(Object object, HierarchicalStreamWriter hsw, MarshallingContext mc) {
            MeshQualifierName descriptorName = (MeshQualifierName) object;
            hsw.addAttribute("MajorTopicYN", descriptorName.getMajorTopicYn());
            hsw.setValue(descriptorName.getName());
        }

        /*
         * (non-Javadoc)
         *
         * @see com.thoughtworks.xstream.converters.Converter#unmarshal(com.thoughtworks.xstream.io.
         * HierarchicalStreamReader, com.thoughtworks.xstream.converters.UnmarshallingContext)
         */
        @Override
        public Object unmarshal(HierarchicalStreamReader hsr, UnmarshallingContext umc) {
            MeshQualifierName descriptorName = new MeshQualifierName();
            descriptorName.setMajorTopicYn(hsr.getAttribute("MajorTopicYN"));
            descriptorName.setName(hsr.getValue());
            return descriptorName;
        }

    }

    /**
     * <pre>
     * <!ELEMENT	DescriptorName (#PCDATA)>
     * <!ATTLIST	DescriptorName
     *                 MajorTopicYN (Y | N) "N"
     *                 Type (Geographic) #IMPLIED>
     * </pre>
     *
     * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
     */
    @Setter
    @Getter
    @ToString
    @XStreamAlias("DescriptorName")
    @XStreamConverter(MeshDescriptorNameConverter.class)
    public static class MeshDescriptorName {
        @XStreamAlias("MajorTopicYN")
        @XStreamAsAttribute
        private String majorTopicYn;

        @XStreamAlias("Type")
        @XStreamAsAttribute
        private String type;

        private String name;

        public String getMajorTopicYn() {
            return majorTopicYn;
        }

        public void setMajorTopicYn(String majorTopicYn) {
            this.majorTopicYn = majorTopicYn;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }

    public static class MeshDescriptorNameConverter implements Converter {

        /*
         * (non-Javadoc)
         *
         * @see com.thoughtworks.xstream.converters.ConverterMatcher#canConvert(java.lang.Class)
         */
        @Override
        public boolean canConvert(Class clazz) {
            return MeshDescriptorName.class.equals(clazz);
        }

        /*
         * (non-Javadoc)
         *
         * @see com.thoughtworks.xstream.converters.Converter#marshal(java.lang.Object,
         * com.thoughtworks.xstream.io.HierarchicalStreamWriter,
         * com.thoughtworks.xstream.converters.MarshallingContext)
         */
        @Override
        public void marshal(Object object, HierarchicalStreamWriter hsw, MarshallingContext mc) {
            MeshDescriptorName descriptorName = (MeshDescriptorName) object;
            hsw.addAttribute("MajorTopicYN", descriptorName.getMajorTopicYn());
            hsw.addAttribute("Type", descriptorName.getType());
            hsw.setValue(descriptorName.getName());
        }

        /*
         * (non-Javadoc)
         *
         * @see com.thoughtworks.xstream.converters.Converter#unmarshal(com.thoughtworks.xstream.io.
         * HierarchicalStreamReader, com.thoughtworks.xstream.converters.UnmarshallingContext)
         */
        @Override
        public Object unmarshal(HierarchicalStreamReader hsr, UnmarshallingContext umc) {
            MeshDescriptorName descriptorName = new MeshDescriptorName();
            descriptorName.setMajorTopicYn(hsr.getAttribute("MajorTopicYN"));
            descriptorName.setType(hsr.getAttribute("Type"));
            descriptorName.setName(hsr.getValue());
            return descriptorName;
        }

    }

    /**
     * <pre>
     * <!ELEMENT	CommentsCorrectionsList (CommentsCorrections+)>
     * </pre>
     *
     * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
     */
    @Getter
    @ToString
    @XStreamAlias("CommentsCorrections")
    public static class CommentsCorrectionsList {
        @XStreamImplicit
        private List<CommentsCorrections> commentsCorrections;
    }

    /**
     * <pre>
     * <!ELEMENT	CommentsCorrections (RefSource,PMID?,Note?)>
     * <!ATTLIST   CommentsCorrections
     *                 RefType (CommentOn | CommentIn | ErratumIn | ErratumFor |
     *                 PartialRetractionIn | PartialRetractionOf | RepublishedFrom |
     *                 RepublishedIn | RetractionOf | RetractionIn | UpdateIn |
     *                 UpdateOf | SummaryForPatientsIn | OriginalReportIn |
     *                 ReprintOf | ReprintIn | Cites) #REQUIRED >
     * <!ELEMENT	RefSource (#PCDATA)>
     * <!ELEMENT	Note (#PCDATA)>
     * </pre>
     *
     * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
     */
    @Getter
    @ToString
    @XStreamAlias("CommentsCorrections")
    public static class CommentsCorrections {
        @XStreamAlias("RefType")
        @XStreamAsAttribute
        private String refType;

        @XStreamAlias("RefSource")
        private String refSource;

        @XStreamAlias("Note")
        private String note;

        @XStreamAlias("PMID")
        private PubMedId pmid;
    }

    /**
     * <pre>
     * <!ELEMENT	KeywordList (Keyword+)>
     * <!ATTLIST	KeywordList Owner (NLM | NLM-AUTO | NASA | PIP | KIE | NOTNLM | HHS) "NLM">
     * </pre>
     *
     * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
     */
    @Getter
    @ToString
    @XStreamAlias("KeywordList")
    public static class KeywordList {
        @XStreamImplicit
        private List<Keyword> keywords;
    }

    /**
     * <pre>
     * <!ELEMENT	Keyword (#PCDATA)>
     * <!ATTLIST	Keyword MajorTopicYN (Y | N) "N">
     * </pre>
     *
     * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
     */
    @Setter
    @Getter
    @ToString
    @XStreamAlias("Keyword")
    @XStreamConverter(KeywordConverter.class)
    public static class Keyword {
        @XStreamAlias("MajorTopicYN")
        @XStreamAsAttribute
        private String majorTopicYn;

        private String keyword;

        public String getMajorTopicYn() {
            return majorTopicYn;
        }

        public void setMajorTopicYn(String majorTopicYn) {
            this.majorTopicYn = majorTopicYn;
        }

        public String getKeyword() {
            return keyword;
        }

        public void setKeyword(String keyword) {
            this.keyword = keyword;
        }
    }

    public static class KeywordConverter implements Converter {

        /*
         * (non-Javadoc)
         *
         * @see com.thoughtworks.xstream.converters.ConverterMatcher#canConvert(java.lang.Class)
         */
        @Override
        public boolean canConvert(Class clazz) {
            return Keyword.class.equals(clazz);
        }

        /*
         * (non-Javadoc)
         *
         * @see com.thoughtworks.xstream.converters.Converter#marshal(java.lang.Object,
         * com.thoughtworks.xstream.io.HierarchicalStreamWriter,
         * com.thoughtworks.xstream.converters.MarshallingContext)
         */
        @Override
        public void marshal(Object object, HierarchicalStreamWriter hsw, MarshallingContext mc) {
            Keyword keyword = (Keyword) object;
            hsw.addAttribute("MajorTopicYN", keyword.majorTopicYn);
            hsw.setValue(keyword.keyword);
        }

        /*
         * (non-Javadoc)
         *
         * @see com.thoughtworks.xstream.converters.Converter#unmarshal(com.thoughtworks.xstream.io.
         * HierarchicalStreamReader, com.thoughtworks.xstream.converters.UnmarshallingContext)
         */
        @Override
        public Object unmarshal(HierarchicalStreamReader hsr, UnmarshallingContext umc) {
            Keyword keyword = new Keyword();
            keyword.setMajorTopicYn(hsr.getAttribute("MajorTopicYN"));
            keyword.setKeyword(hsr.getValue());
            return keyword;
        }

    }

    /**
     * <pre>
     * <!ELEMENT	GeneralNote (#PCDATA)>
     * <!ATTLIST	GeneralNote Owner (NLM | NASA | PIP | KIE | HSR | HMD) "NLM">
     * </pre>
     *
     * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
     */
    @Setter
    @Getter
    @ToString
    @XStreamAlias("GeneralNote")
    @XStreamConverter(GeneralNoteConverter.class)
    public static class GeneralNote {
        @XStreamAlias("Owner")
        @XStreamAsAttribute
        private String owner;

        private String note;

        public String getOwner() {
            return owner;
        }

        public void setOwner(String owner) {
            this.owner = owner;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }
    }

    public static class GeneralNoteConverter implements Converter {

        /*
         * (non-Javadoc)
         *
         * @see com.thoughtworks.xstream.converters.ConverterMatcher#canConvert(java.lang.Class)
         */
        @Override
        public boolean canConvert(Class clazz) {
            return GeneralNote.class.equals(clazz);
        }

        /*
         * (non-Javadoc)
         *
         * @see com.thoughtworks.xstream.converters.Converter#marshal(java.lang.Object,
         * com.thoughtworks.xstream.io.HierarchicalStreamWriter,
         * com.thoughtworks.xstream.converters.MarshallingContext)
         */
        @Override
        public void marshal(Object object, HierarchicalStreamWriter hsw, MarshallingContext mc) {
            GeneralNote note = (GeneralNote) object;
            hsw.addAttribute("Owner", note.getOwner());
            hsw.setValue(note.getNote());
        }

        /*
         * (non-Javadoc)
         *
         * @see com.thoughtworks.xstream.converters.Converter#unmarshal(com.thoughtworks.xstream.io.
         * HierarchicalStreamReader, com.thoughtworks.xstream.converters.UnmarshallingContext)
         */
        @Override
        public Object unmarshal(HierarchicalStreamReader hsr, UnmarshallingContext umc) {
            GeneralNote note = new GeneralNote();
            note.setOwner(hsr.getAttribute("Owner"));
            note.setNote(hsr.getValue());
            return note;
        }

    }

    /**
     * <pre>
     * <!ELEMENT	GeneSymbolList (GeneSymbol+)>
     * <!ELEMENT	GeneSymbol (#PCDATA)>
     * </pre>
     *
     * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
     */
    @Getter
    @ToString
    public static class GeneSymbolList {
        @XStreamImplicit(itemFieldName = "GeneSymbol")
        private List<String> geneSymbols;
    }

}

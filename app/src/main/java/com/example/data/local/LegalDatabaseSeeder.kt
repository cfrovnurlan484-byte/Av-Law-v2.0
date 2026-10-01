package com.example.data.local

import com.example.data.local.model.ArticleEntity
import com.example.data.local.model.CaseStudy
import com.example.data.local.model.ExamResultEntity
import com.example.data.local.model.LegalSourceEntity
import com.example.data.local.model.UserProfile

object LegalDatabaseSeeder {
    // Every new user starts with 0 points and empty stats
    fun getInitialProfile(): UserProfile = UserProfile(
        id = 1,
        fullName = "Hüquqşünas",
        rankTitle = "Stajor Hüquqşünas",
        email = "",
        totalPoints = 0,
        theoryChecksCompleted = 0,
        casesSolved = 0,
        appealsWon = 0,
        appealsLost = 0,
        pointsWonFromAppeals = 0,
        pointsLostFromAppeals = 0
    )

    // Initial legal case studies for CaseStudy entity
    fun getInitialCaseStudies(): List<CaseStudy> = listOf(
        CaseStudy(
            title = "Notariat Təsdiqsiz Mənzil Alqı-Satqısı",
            category = "Mülki Hüquq",
            description = "Daşınmaz əmlakın dövlət qeydiyyatı və sadə yazılı müqavilənin etibarsızlığı mübahisəsi.",
            status = "ACTIVE",
            parties = "İddiaçı: Ə. Həsənov, Cavabdeh: R. Məmmədov",
            facts = "Tərəflər 2023-cü ildə mənzilin satışı barədə sadə yazılı formada razılaşma bağlayıb, lakin notariat təsdiqi və daşınmaz əmlakın dövlət reyestrində qeydiyyat aparılmayıb.",
            legalIssue = "AR Mülki Məcəlləsinin 139, 182 və 337-ci maddələri baxımından müqavilənin hüquqi qüvvəsi və restitusiya qaydaları.",
            difficulty = "Orta",
            pointsReward = 50
        ),
        CaseStudy(
            title = "Zəruri Müdafiə və Müdafiə Hədləri",
            category = "Cinayət Hüququ",
            description = "Gecə vaxtı fərdi yaşayış evinə qanunsuz basqın zamanı vurulmuş bədən xəsarətinin tövsifi.",
            status = "ACTIVE",
            parties = "Təqsirləndirilən: T. Qasımov, Zərərçəkmiş: V. Babayev",
            facts = "Gecə saatlarında qanunsuz daxil olan şəxsə qarşı mülk sahibi tərəfindən fiziki güc tətbiq edilmiş və ağır xəsarət yetirilmişdir.",
            legalIssue = "AR CM 36-cı maddəsi: Zəruri müdafiə şərtləri və həddin aşılması meyarları.",
            difficulty = "Çətin",
            pointsReward = 75
        ),
        CaseStudy(
            title = "Ştat İxtisarı və Hamilə İşçinin Hüququ",
            category = "Əmək Hüququ",
            description = "Ştat ixtisarı zamanı hamilə qadının əmək müqaviləsinə birtərəfli xitam verilməsinin qanuniliyi.",
            status = "ACTIVE",
            parties = "İddiaçı: N. Quliyeva, Cavabdeh: QSC",
            facts = "İşəgötürən müvafiq struktur bölmənin ləğv edilməsi səbəbilə 4 aylıq hamilə olan işçinin müqaviləsinə xitam vermişdir.",
            legalIssue = "AR Əmək Məcəlləsinin 78 və 79-cu maddələrində təsbit edilmiş qadağalar və işə bərpa tələbi.",
            difficulty = "Orta",
            pointsReward = 50
        ),
        CaseStudy(
            title = "Yüksək Təhlükə Mənbəyinin Vurduğu Zərər",
            category = "Mülki Hüquq",
            description = "Yol-nəqliyyat hadisəsi nəticəsində piyadaya dəymiş maddi və mənəvi zərərin kompensasiyası.",
            status = "ACTIVE",
            parties = "İddiaçı: S. Vəliyev, Cavabdeh: Logistika MMC",
            facts = "Nizamlanmayan piyada keçidində yük maşınının vurması nəticəsində piyada orta ağır xəsarət almış, müalicə və mənəvi təzminat tələb etmişdir.",
            legalIssue = "AR MM 1111 və 1115-ci maddələri: Təqsirsiz məsuliyyət və mənəvi zərərin ağlabatan məbləğdə müəyyənləşdirilməsi.",
            difficulty = "Asan",
            pointsReward = 40
        )
    )

    // No simulated exam results
    fun getInitialExams(): List<ExamResultEntity> = emptyList()

    // No fake community posts
    fun getInitialArticles(): List<ArticleEntity> = emptyList()

    // Authentic AR Legislation for Legal Library
    fun getInitialLegalSources(): List<LegalSourceEntity> = listOf(
        // AR Konstitusiyası
        LegalSourceEntity(
            codeCategory = "AR Konstitusiyası",
            articleNumber = "Maddə 12",
            title = "Dövlətin ali məqsədi",
            content = "I. İnsan və vətəndaş hüquqlarının və azadlıqlarının, Azərbaycan Respublikasının vətəndaşlarına layiqli həyat səviyyəsinin təmin edilməsi dövlətin ali məqsədidir.\nII. Bu Konstitusiyada təsbit edilmiş insan və vətəndaş hüquqları və azadlıqları Azərbaycan Respublikasının tərəfdar çıxdığı beynəlxalq müqavilələrə uyğun tətbiq edilir.",
            keywords = "dövlətin ali məqsədi, insan hüquqları, layiqli həyat, beynəlxalq müqavilələr"
        ),
        LegalSourceEntity(
            codeCategory = "AR Konstitusiyası",
            articleNumber = "Maddə 28",
            title = "Azadlıq hüququ",
            content = "I. Hər kəsin azadlıq hüququ vardır.\nII. Azadlıq hüququ yalnız qanunla nəzərdə tutulmuş qaydada tutulma, həbsəalma və ya azadlıqdan məhrumetmə yolu ilə məhdudlaşdırıla bilər.\nIII. Qanuni əsas olmadan heç kəs tutula, həbsə alına və ya azadlıqdan məhrum edilə bilməz.",
            keywords = "azadlıq hüququ, tutulma, həbs, məhdudlaşdırma, qanuni əsas"
        ),
        LegalSourceEntity(
            codeCategory = "AR Konstitusiyası",
            articleNumber = "Maddə 60",
            title = "Hüquq və azadlıqların məhkəmə təminatı",
            content = "I. Hər kəsin hüquq və azadlıqlarının məhkəmədə müdafiəsinə təminat verilir.\nII. Hər kəs dövlət orqanlarının, siyasi partiyaların, hüquqi şəxslərin, bələdiyyələrin və vəzifəli şəxslərin hərəkətlərindən və ya hərəkətsizliyindən məhkəməyə şikayət edə bilər.\nIII. Hər kəsin onun işinə qanunla müəyyən edilmiş səlahiyyətli, müstəqil və qərəzsiz məhkəmə tərəfindən ağlabatan müddətdə baxılması hüququ vardır.",
            keywords = "məhkəmə təminatı, müdafiə, şikayət, ağlabatan müddət, qərəzsiz məhkəmə"
        ),
        LegalSourceEntity(
            codeCategory = "AR Konstitusiyası",
            articleNumber = "Maddə 63",
            title = "Təqsirsizlik prezumpsiyası",
            content = "I. Hər kəsin təqsirsizlik prezumpsiyası hüququ vardır. Cinayətin törədilməsində təqsirləndirilən hər bir şəxs, onun təqsiri qanunla nəzərdə tutulan qaydada sübuta yetirilməyibsə və bu barədə məhkəmənin qanuni qüvvəyə minmiş hökmü yoxdursa, təqsirsiz sayılır.\nII. Şəxsin təqsirli olduğuna əsaslı şübhələr varsa, onun təqsirli bilinməsinə yol verilmir.\nIII. Cinayətin törədilməsində təqsirləndirilən şəxs özünün təqsirsizliyini sübuta yetirməyə borclu deyildir.",
            keywords = "təqsirsizlik prezumpsiyası, sübut, şübhə, qanuni qüvvə, məhkəmə hökmü"
        ),

        // Mülki Məcəllə
        LegalSourceEntity(
            codeCategory = "Mülki Məcəllə",
            articleNumber = "Maddə 139",
            title = "Daşınmaz əmlak üzərində hüquqların dövlət qeydiyyatı",
            content = "139.1. Daşınmaz əmlaka mülkiyyət hüququ və digər əşya hüquqları, bu hüquqların məhdudlaşdırılması, onların əmələ gəlməsi, başqasına keçməsi və xitamı dövlət qeydiyyatına alınmalıdır.\n139.2. Qeydiyyat daşınmaz əmlakın dövlət reyestrində aparılır və qeydiyyat anından hüquq əldə edilmiş sayılır.",
            keywords = "daşınmaz əmlak, dövlət qeydiyyatı, daşınmaz əmlakın dövlət reyestri, mülkiyyət"
        ),
        LegalSourceEntity(
            codeCategory = "Mülki Məcəllə",
            articleNumber = "Maddə 337",
            title = "Əhəmiyyətsiz və mübahisələndirilən əqdlər",
            content = "337.1. Əqd bu Məcəllə ilə müəyyənləşdirilmiş əsaslara görə məhkəmə tərəfindən etibarsız hesab edilməsindən asılı olmayaraq etibarsız sayıldıqda (əhəmiyyətsiz əqd) və ya məhkəmə tərəfindən etibarsız hesab edildikdə (mübahisələndirilən əqd) etibarsızdır.\n337.2. Əhəmiyyətsiz əqdin etibarsızlığının nəticələrinin tətbiqi haqqında tələbi hər bir maraqlı şəxs irəli sürə bilər. Məhkəmə bu cür nəticələri öz təşəbbüsü ilə də tətbiq edə bilər.",
            keywords = "əqd, əhəmiyyətsiz əqd, mübahisələndirilən əqd, etibarsızlıq, restitusiya"
        ),
        LegalSourceEntity(
            codeCategory = "Mülki Məcəllə",
            articleNumber = "Maddə 338",
            title = "Etibarsız əqdin nəticələri (Restitusiya)",
            content = "338.1. Etibarsız əqd onun etibarsızlığı ilə bağlı nəticələr istisna olmaqla, hüquqi nəticələrə səbəb olmur və bağlandığı andan etibarsızdır.\n338.2. Əqd etibarsız olduqda, əgər bu Məcəllədə onun etibarsızlığının digər nəticələri nəzərdə tutulmayıbsa, tərəflərin hər biri əqd üzrə aldığı hər şeyi digər tərəfə qaytarmağa (ikitərəfli restitusiya), alınanları eyni ilə qaytarmaq mümkün olmadıqda isə onun dəyərini pulla ödəməyə borcludur.",
            keywords = "restitusiya, ikitərəfli restitusiya, ilkin vəziyyət, kompensasiya"
        ),
        LegalSourceEntity(
            codeCategory = "Mülki Məcəllə",
            articleNumber = "Maddə 422",
            title = "Şəraitin əhəmiyyətli dərəcədə dəyişməsi ilə əlaqədar müqavilənin dəyişdirilməsi və ləğvi",
            content = "422.1. Tərəflərin müqavilə bağlayarkən əsas götürdükləri şəraitin əhəmiyyətli dərəcədə dəyişməsi, əgər müqavilədə ayrı qayda nəzərdə tutulmayıbsa və ya onun mahiyyətindən irəli gəlmirsə, müqavilənin dəyişdirilməsi və ya ləğv edilməsi üçün əsasdır.\n422.2. Əgər şərait elə dəyişmişdirsə ki, tərəflər bunu ağlabatan şəkildə əvvəlcədən görə bilsəydilər, müqaviləni heç bağlamaz və ya xeyli fərqli şərtlərlə bağlayardılar.",
            keywords = "şəraitin dəyişməsi, müqavilənin ləğvi, fors-major, ağlabatan qabaqlama"
        ),

        // Cinayət Məcəlləsi
        LegalSourceEntity(
            codeCategory = "Cinayət Məcəlləsi",
            articleNumber = "Maddə 36",
            title = "Zəruri müdafiə",
            content = "36.1. Zəruri müdafiə vəziyyətində, yəni müdafiə edənin və ya başqa şəxsin həyatını, sağlamlığını və hüquqlarını, cəmiyyətin və ya dövlətin qanunla qorunan mənafelərini hücum edənə zərər vurmaq yolu ilə ictimai təhlükəli qəsddən qoruyarkən törədilmiş əməl cinayət sayılmır.\n36.2. Zəruri müdafiə həddinin aşılması, yəni müdafiənin qəsdin xarakterinə və ictimai təhlükəlilik dərəcəsinə açıq-aşkar uyğun gəlməməsi yalnız qəsdən zərər vurulduqda cinayət məsuliyyətinə səbəb olur.",
            keywords = "zəruri müdafiə, ictimai təhlükəli qəsd, müdafiə həddinin aşılması, qanunla qorunan maraqlar"
        ),
        LegalSourceEntity(
            codeCategory = "Cinayət Məcəlləsi",
            articleNumber = "Maddə 38",
            title = "Son zərurət",
            content = "38.1. Son zərurət vəziyyətində, yəni şəxsin və ya digər şəxslərin həyatına, sağlamlığına, hüquqlarına bilavasitə təhlükə törədən halı aradan qaldırmaq üçün qanunla qorunan mənafelərə zərər vurmaqla törədilmiş əməl cinayət sayılmır, bir şərtlə ki, bu təhlükəni başqa vasitələrlə aradan qaldırmaq mümkün olmasın və vurulmuş zərər qarşısı alınmış zərərdən az olsun.",
            keywords = "son zərurət, təhlükə, zərərlərin nisbəti, qanunla qorunan mənafelər"
        ),

        // Əmək Məcəlləsi
        LegalSourceEntity(
            codeCategory = "Əmək Məcəlləsi",
            articleNumber = "Maddə 78",
            title = "İşçilərin sayının və ya ştatların ixtisarı zamanı işdə saxlanmaqda üstünlük hüququ",
            content = "78.1. Müvafiq ixtisas üzrə əmək məhsuldarlığı daha yüksək olan işçilər işdə saxlanmaq üçün üstünlük hüququna malikdirlər.\n78.2. Əmək məhsuldarlığı və ixtisası eyni olduqda: ailəsində iki və daha çox himayəsində olan şəxs olanlara, həmin müəssisədə əmək şikəstliyi almış şəxslərə, müharibə iştirakçılarına və şəhid ailəsi üzvlərinə üstünlük verilir.",
            keywords = "ştat ixtisarı, üstünlük hüququ, əmək məhsuldarlığı, ixtisas, şəhid ailəsi"
        ),
        LegalSourceEntity(
            codeCategory = "Əmək Məcəlləsi",
            articleNumber = "Maddə 79",
            title = "Əmək müqaviləsinin ləğv edilməsi qadağan olunan işçilər",
            content = "79.1. İşəgötürən tərəfindən: hamilə və 3 yaşınadək uşağı olan qadınların, yeganə qazanc mənbəyi həmin müəssisə olan və məktəb yaşınadək uşağını təkbaşına böyüdən işçilərin, əmək qabiliyyətini müvəqqəti itirən işçilərin, məzuniyyətdə və ya ezamiyyətdə olan işçilərin əmək müqaviləsinin ləğv edilməsi qadağandır.",
            keywords = "əmək müqaviləsi, ləğv qadağası, hamilə qadın, 3 yaşınadək uşaq, məzuniyyət"
        ),

        // İnzibati Xətalar Məcəlləsi
        LegalSourceEntity(
            codeCategory = "İnzibati Xətalar",
            articleNumber = "Maddə 52",
            title = "İnzibati xəta haqqında protokol",
            content = "52.1. İnzibati xətanın törədilməsi faktı üzrə səlahiyyətli vəzifəli şəxs tərəfindən dərhal protokol tərtib edilir.\n52.2. Protokolda: onun tərtib edildiyi tarix və yer, tərtib edən şəxsin vəzifəsi və adı, inzibati xəta törətmiş şəxs haqqında məlumatlar, inzibati xətanın mahiyyəti və İXM-in müvafiq maddəsi göstərilməlidir.",
            keywords = "inzibati xəta, protokol, səlahiyyətli vəzifəli şəxs, sübut"
        )
    )
}

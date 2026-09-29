package com.example.data.local

import com.example.data.local.model.ArticleEntity
import com.example.data.local.model.ExamResultEntity
import com.example.data.local.model.LegalSourceEntity
import com.example.data.local.model.UserProfile

object LegalDatabaseSeeder {
    fun getInitialProfile(): UserProfile = UserProfile(
        id = 1,
        fullName = "Əli Məmmədov",
        rankTitle = "Təcrübəçi Hüquqşünas",
        email = "ali.mammadov@law.az",
        totalPoints = 480,
        theoryChecksCompleted = 6,
        casesSolved = 4,
        appealsWon = 2,
        appealsLost = 1,
        pointsWonFromAppeals = 120,
        pointsLostFromAppeals = 30
    )

    fun getInitialExams(): List<ExamResultEntity> = listOf(
        ExamResultEntity(
            id = 1,
            examType = "THEORY",
            title = "Mülki Hüquq: Əqdlərin Etibarsızlığı və Restitusiya",
            topicCategory = "Mülki Hüquq",
            userAnswerText = "Əhəmiyyətsiz əqdlər bağlandığı andan etibarsızdır. Mübahisələndirilən əqdlər isə məhkəmə tərəfindən etibarsız hesab edilir. Restitusiya nəticəsində tərəflər əqdin bağlanmasına qədərki ilkin vəziyyətə qaytarılır.",
            score = 88,
            verdict = "Müvəffəqiyyətli (Əla)",
            accuracyScore = 32,
            terminologyScore = 23,
            reasoningScore = 21,
            fluencyScore = 12,
            feedback = "Mülki Məcəllənin 337 və 182-ci maddələrinə istinad çox aydındır. Əhəmiyyətsiz və mübahisələndirilən əqdlərin fərqi dəqiq vurğulanıb.",
            recommendations = "Gələcəkdə vicdanlı əldəedənin hüquqlarının qorunması məsələsinə də (MM m. 182.3) toxunmaq tövsiyə edilir.",
            appealStatus = "NONE",
            createdAt = System.currentTimeMillis() - 86400000L * 3
        ),
        ExamResultEntity(
            id = 2,
            examType = "CASE",
            title = "Kazus: Notariat təsdiqi olmayan mənzil alqı-satqısı",
            topicCategory = "Mülki Hüquq",
            userAnswerText = "Mənzilin alqı-satqısı mütləq notariat qaydasında təsdiq olunmalı və daşınmaz əmlakın dövlət reyestrində qeydiyyata alınmalıdır. Qanunun tələb etdiyi formaya riayət edilmədikdə əqd əhəmiyyətsiz sayılır.",
            score = 74,
            verdict = "Müvəffəqiyyətli (Yaxşı)",
            accuracyScore = 26,
            terminologyScore = 19,
            reasoningScore = 18,
            fluencyScore = 11,
            feedback = "Faktiki tövsif düzgündür, lakin kompensasiya və çəkilmiş xərclərin geri tələb olunması mexanizmi tam izah edilməyib.",
            recommendations = "Mülki Məcəllənin 139 və 337.1-ci maddələri ilə yanaşı, əsassız varlanma institutuna da diqqət yetirin.",
            appealStatus = "WON",
            appealWager = 30,
            appealReason = "İzahımda kompensasiya tələbinin əsassız varlanma müddəaları ilə tənzimləndiyini qeyd etmişdim.",
            appealFeedback = "Apellyasiya Kollegiyası müraciətə yenidən baxdı: Əsaslandırma qəbul edildi. Xal +10 artırıldı və qoyulmuş 30 xal bərpa olunaraq +15 bonus təqdim edildi.",
            createdAt = System.currentTimeMillis() - 86400000L * 2
        )
    )

    fun getInitialArticles(): List<ArticleEntity> = listOf(
        ArticleEntity(
            id = 1,
            title = "Mülki Məcəllənin 337-ci maddəsi: Əhəmiyyətsiz və Mübahisələndirilən Əqdlər",
            authorName = "Rəşad Quliyev",
            authorRank = "Vəkillər Kollegiyasının Üzvü",
            category = "Mülki Hüquq",
            summary = "Azərbaycan məhkəmə təcrübəsində bağlandığı andan hüquqi nəticə doğurmayan əqdlərlə məhkəmə tərəfindən ləğv edilən əqdlərin fərqləndirilməsi.",
            content = """Azərbaycan Respublikasının Mülki Məcəlləsinin 337-ci maddəsinə əsasən, qanunla müəyyənləşdirilmiş əsaslara görə əqd məhkəmə tərəfindən etibarsız hesab edilməsindən asılı olmayaraq etibarsız sayıldıqda (əhəmiyyətsiz əqd) və ya məhkəmə tərəfindən etibarsız hesab edildikdə (mübahisələndirilən əqd) etibarsızdır.

Praktiki Əhəmiyyət:
1. Əhəmiyyətsiz əqd heç bir hüquqi nəticə doğurmur və tərəflər ilkin vəziyyətə qayıtmalıdır (ikitərəfli restitusiya).
2. Mübahisələndirilən əqdlər isə maraqlı şəxsin məhkəməyə müraciəti nəticəsində etibarsız sayıla bilər.

Ali Məhkəmənin yanaşması göstərir ki, iddia müddətlərinin hesablanması da əqdin bu iki növünə görə fərqlənir. Əhəmiyyətsiz əqdlərin nəticələrinin aradan qaldırılması üçün iddia müddəti icraya başlanıldığı gündən hesablanır.""",
            legalCitations = "AR Mülki Məcəlləsi m. 182, 337, 338; Konstitusiya Məhkəməsinin Qərarı",
            likesCount = 42,
            isLiked = false,
            isPinned = true,
            readMinutes = 5,
            isUserCreated = false,
            createdAt = System.currentTimeMillis() - 86400000L * 4
        ),
        ArticleEntity(
            id = 2,
            title = "Zəruri Müdafiə və Həddi Aşma: AR CM 36-cı maddəsinin tətbiqi",
            authorName = "Leyla Əliyeva",
            authorRank = "Hüquq Magistri, BDU",
            category = "Cinayət Hüququ",
            summary = "Hücum edən şəxsin həyat və sağlamlığına zərər yetirilməsinin qanuniliyi və qəsdin xarakterinə uyğunluq meyarları.",
            content = """Cinayət Məcəlləsinin 36-cı maddəsinə əsasən, hücum edənə zərər vurmaqla qorunan maraqların müdafiəsi zamanı törədilmiş əməl cinayət sayılmır.

Lakin zəruri müdafiə həddinin aşılması (müdafiənin hücumun xarakterinə və təhlükəlilik dərəcəsinə açıq-aşkar uyğun gəlməməsi) cinayət məsuliyyətinə səbəb olur.

Məhkəmə istintaqında əsas diqqət hücumun qəfil olması, tərəflərin fiziki gücü, silah və ya vasitələrin istifadəsi və hücum edənin real niyyətinə yetirilməlidir.""",
            legalCitations = "AR Cinayət Məcəlləsi m. 36, 37; Ali Məhkəmənin Plenum Qərarı №2",
            likesCount = 29,
            isLiked = true,
            isPinned = false,
            readMinutes = 4,
            isUserCreated = false,
            createdAt = System.currentTimeMillis() - 86400000L * 2
        ),
        ArticleEntity(
            id = 3,
            title = "İşçilərin Ştat İxtisarı Zamanı İşdə Saxlanmada Üstünlük Hüququ",
            authorName = "Fərid Nəzərov",
            authorRank = "Aparıcı Hüquqşünas",
            category = "Əmək Hüququ",
            summary = "Əmək Məcəlləsinin 78-ci maddəsinin tələbləri: İxtisas və əmək məhsuldarlığı meyarları necə sənədləşdirilməlidir?",
            content = """İşəgötürən tərəfindən ştat ixtisarı həyata keçirilərkən müəyyən tələblərə ciddi əməl edilməlidir.

Əmək Məcəlləsinin 78-ci maddəsinə əsasən, ixtisas dərəcəsi və ya əmək məhsuldarlığı daha yüksək olan işçilər işdə saxlanmaqda üstünlük hüququna malikdirlər.

Bu meyarlar bərabər olduqda isə:
- Şəhid ailəsi üzvləri;
- Ailədə iki və daha çox himayəsində olan şəxslər;
- Əmək şikəstliyi almış şəxslər üstünlüyə malikdir.

Əmək Məcəlləsinin 79-cu maddəsinə görə isə hamilə və 3 yaşınadək uşağı olan qadınların əmək müqaviləsinin işəgötürənin təşəbbüsü ilə ləğvi birbaşa qadağandır.""",
            legalCitations = "AR Əmək Məcəlləsi m. 70, 78, 79",
            likesCount = 37,
            isLiked = false,
            isPinned = false,
            readMinutes = 6,
            isUserCreated = false,
            createdAt = System.currentTimeMillis() - 86400000L
        )
    )

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
            content = "I. Hər kəsin hüquq və azadlıqlarının məhkəmədə müdafiəsinə təminat verilir.\nII. Hər kəs dövlət orqanlarının, siyasi partiyaların, hüquqi şəxslərin və vəzifəli şəxslərin hərəkətlərindən və ya hərəkətsizliyindən məhkəməyə şikayət edə bilər.",
            keywords = "məhkəmə təminatı, müdafiə, məhkəməyə şikayət, vəzifəli şəxslər"
        ),

        // Mülki Məcəllə
        LegalSourceEntity(
            codeCategory = "Mülki Məcəllə",
            articleNumber = "Maddə 139",
            title = "Daşınmaz əmlak üzərində hüquqların dövlət qeydiyyatı",
            content = "139.1. Daşınmaz əmlak üzərində mülkiyyət hüququ və digər əşya hüquqları, bu hüquqların məhdudlaşdırılması, onların əmələ gəlməsi, başqasına keçməsi və xitamı daşınmaz əmlakın dövlət reyestrində qeydə alınmalıdır.\n139.2. Hüquqların qeydiyyatı reyestrə müvafiq qeydin daxil edilməsi anından yaranır.",
            keywords = "daşınmaz əmlak, dövlət reyestri, mülkiyyət hüququ, əşya hüququ, qeydiyyat"
        ),
        LegalSourceEntity(
            codeCategory = "Mülki Məcəllə",
            articleNumber = "Maddə 182",
            title = "Əqdin etibarsızlığının nəticələri",
            content = "182.1. Etibarsız əqd onun etibarsızlığı ilə bağlı nəticələrdən başqa digər hüquqi nəticələrə səbəb olmur və bağlandığı andan etibarsızdır.\n182.2. Əqd etibarsız olduqda, əgər bu Məcəllədə onun etibarsızlığının digər nəticələri nəzərdə tutulmayıbsa, tərəflərdən hər biri əqd üzrə aldığı hər şeyi digər tərəfə qaytarmağa borcludur (ikitərəfli restitusiya).",
            keywords = "əqdin etibarsızlığı, ikitərəfli restitusiya, ilkin vəziyyət, hüquqi nəticələr"
        ),
        LegalSourceEntity(
            codeCategory = "Mülki Məcəllə",
            articleNumber = "Maddə 337",
            title = "Əqdlərin etibarsızlığı",
            content = "337.1. Bu Məcəllə ilə müəyyənləşdirilmiş əsaslara görə əqd məhkəmə tərəfindən etibarsız hesab edilməsindən asılı olmayaraq etibarsız sayıldıqda (əhəmiyyətsiz əqd) və ya məhkəmə tərəfindən etibarsız hesab edildikdə (mübahisələndirilən əqd) etibarsızdır.\n337.2. Əhəmiyyətsiz əqdin etibarsızlığının nəticələrinin tətbiqi haqqında tələbi hər bir maraqlı şəxs irəli sürə bilər.",
            keywords = "əhəmiyyətsiz əqd, mübahisələndirilən əqd, etibarsızlıq növləri, iddia hüququ"
        ),
        LegalSourceEntity(
            codeCategory = "Mülki Məcəllə",
            articleNumber = "Maddə 422",
            title = "Şəraitin əhəmiyyətli dərəcədə dəyişməsi ilə əlaqədar müqavilənin dəyişdirilməsi və ya ləğvi",
            content = "422.1. Tərəflərin müqavilə bağlayarkən əsaslandıqları şəraitin əhəmiyyətli dərəcədə dəyişməsi, əgər müqavilədə ayrı qayda nəzərdə tutulmayıbsa və ya onun mahiyyətindən irəli gəlmirsə, onun dəyişdirilməsi və ya ləğvi üçün əsasdır.\n422.2. Şəraitin dəyişməsi o halda əhəmiyyətli sayılır ki, əgər tərəflər bu dəyişikliyi əvvəlcədən görə bilsəydilər, müqaviləni əhəmiyyətli dərəcədə fərqli şərtlərlə bağlayardılar.",
            keywords = "şəraitin dəyişməsi, müqavilənin ləğvi, fors-major, öhdəliklər"
        ),
        LegalSourceEntity(
            codeCategory = "Mülki Məcəllə",
            articleNumber = "Maddə 1115",
            title = "Mənəvi zərərin ödənilməsi əsasları",
            content = "1115.1. Vətəndaşa onun şəxsi qeyri-əmlak hüquqlarını və ya digər qeyri-maddi nemətlərini pozan hərəkətlərlə (hərəkətsizliklə) vurulmuş mənəvi zərər (fiziki və ya mənəvi iztirablar) təqsirkar şəxs tərəfindən ödənilməlidir.\n1115.2. Mənəvi zərər pul forması ilə ödənilir.",
            keywords = "mənəvi zərər, kompensasiya, qeyri-əmlak hüquqları, iztirab, təqsir"
        ),

        // Cinayət Məcəlləsi
        LegalSourceEntity(
            codeCategory = "Cinayət Məcəlləsi",
            articleNumber = "Maddə 36",
            title = "Zəruri müdafiə",
            content = "36.1. Zəruri müdafiə vəziyyətində, yəni hücum edənə zərər vurmaqla müdafiə edənin və ya başqa şəxsin həyatını, sağlamlığını və hüquqlarını qoruyarkən törədilmiş əməl cinayət sayılmır.\n36.2. Zəruri müdafiə həddini aşma—müdafiənin hücumun xarakterinə və təhlükəlilik dərəcəsinə açıq-aşkar uyğun gəlməməsi, yəni qəsdən törədilmiş zərərin yetirilməsi nəticəsində yaranır.",
            keywords = "zəruri müdafiə, həddi aşma, hücum, qanuni müdafiə, təhlükə"
        ),
        LegalSourceEntity(
            codeCategory = "Cinayət Məcəlləsi",
            articleNumber = "Maddə 37",
            title = "Son zərurət",
            content = "37.1. Son zərurət vəziyyətində, yəni dövlətin, cəmiyyətin və ya şəxsin maraqlarına birbaşa təhdid edən təhlükəni başqa vasitələrlə aradan qaldırmaq mümkün olmadıqda törədilən əməl cinayət sayılmır, bir şərtlə ki, vurulmuş zərər qarşısı alınmış zərərdən az olsun.",
            keywords = "son zərurət, təhlükə, zərərin nisbəti, qaçılmaz təhlükə"
        ),
        LegalSourceEntity(
            codeCategory = "Cinayət Məcəlləsi",
            articleNumber = "Maddə 178",
            title = "Dələduzluq",
            content = "178.1. Dələduzluq, yəni etibardan sui-istifadə etmə və ya aldatma yolu ilə özgənin əmlakını ələ keçirmə və ya əmlak hüquqlarını əldə etmə—müəyyən edilmiş miqdarda cərimə və ya iki ilədək müddətə azadlıqdan məhrum etmə ilə cəzalandırılır.",
            keywords = "dələduzluq, etibardan sui-istifadə, aldatma, əmlak cinayətləri"
        ),

        // Əmək Məcəlləsi
        LegalSourceEntity(
            codeCategory = "Əmək Məcəlləsi",
            articleNumber = "Maddə 78",
            title = "İşçilərin sayının və ya ştatların ixtisar edilməsi zamanı işdə saxlanmaqda üstünlük hüququ",
            content = "78.1. Müvafiq vəzifələr üzrə ixtisas dərəcəsi və ya əmək məhsuldarlığı daha yüksək olan işçilər işdə saxlanmaqda üstünlük hüququna malikdirlər.\n78.2. İxtisas dərəcəsi və ya əmək məhsuldarlığı bərabər olduqda ailə tərkibi, himayəsində olan şəxslər və əmək şikəstliyi nəzərə alınır.",
            keywords = "ştat ixtisarı, üstünlük hüququ, əmək məhsuldarlığı, ixtisas dərəcəsi"
        ),
        LegalSourceEntity(
            codeCategory = "Əmək Məcəlləsi",
            articleNumber = "Maddə 79",
            title = "Əmək müqaviləsinin ləğv edilməsi qadağan olunan işçilər",
            content = "79.1. İşəgötürən tərəfindən hamilə, habelə üç yaşınadək uşağı olan qadınların, üç yaşınadək uşağını təkbaşına böyüdən kişilərin əmək müqaviləsinin ləğv edilməsi qadağandır (müəssisənin ləğvi halları istisna olmaqla).",
            keywords = "əmək müqaviləsi, hamilə qadınlar, azyaşlı uşaq, ləğv qadağası"
        ),

        // İnzibati Xətalar Məcəlləsi
        LegalSourceEntity(
            codeCategory = "İnzibati Xətalar",
            articleNumber = "Maddə 52",
            title = "İnzibati xəta haqqında protokol",
            content = "52.1. İnzibati xəta törədildikdə, bu Məcəllədə nəzərdə tutulmuş hallar istisna olmaqla, səlahiyyətli vəzifəli şəxs tərəfindən inzibati xəta haqqında protokol tərtib edilir.\n52.2. Protokolda xətanın mahiyyəti, törədildiyi yer, vaxt və şəxsin izahatı qeyd olunur.",
            keywords = "inzibati protokol, vəzifəli şəxs, izahat, xətanın mahiyyəti"
        ),

        // Məhkəmə Plenum Qərarları
        LegalSourceEntity(
            codeCategory = "Məhkəmə Qərarları",
            articleNumber = "Plenum Qərarı №3",
            title = "Mənəvi zərərin ödənilməsi barədə qanunvericiliyin tətbiqi təcrübəsi haqqında",
            content = "Ali Məhkəmənin Plenumu qeyd edir ki, mənəvi zərərin məbləği müəyyən edilərkən məhkəmələr hüquq pozuntusunun ağırlıq dərəcəsini, zərərçəkənə vurulmuş mənəvi iztirabların dərinliyini, təqsirin dərəcəsini və ədalətlilik, ağlabatanlıq prinsiplərini əsas götürməlidirlər.",
            keywords = "ali məhkəmə, plenum, mənəvi zərər, ağlabatanlıq, proporsionallıq"
        )
    )
}

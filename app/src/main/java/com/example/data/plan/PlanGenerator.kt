package com.example.data.plan

import com.example.data.local.entities.PlanEntity
import com.example.data.local.entities.StudySessionEntity
import java.util.Calendar
import java.util.UUID
import kotlin.math.ceil

/**
 * Generates personalized, beginner-friendly study plans.
 * Calculates mathematically sound completion dates based on selected study days.
 * Intersperses periodic Grace/Catch-Up days to eliminate guilt or shame.
 */
object PlanGenerator {

    data class PlanWithSessions(
        val plan: PlanEntity,
        val sessions: List<StudySessionEntity>
    )

    fun generatePlan(
        bibleExperience: String,
        spiritualGoal: String,
        minutesPerSession: Int,
        studyDaysPerWeek: Int,
        planStyle: String,
        startDateMillis: Long = System.currentTimeMillis()
    ): PlanWithSessions {
        val planId = "plan_${UUID.randomUUID().toString().take(8)}"

        // Curated beginner curriculum for Gospel of John
        val rawCurriculum = getJohnCurriculum()

        val generatedSessions = mutableListOf<StudySessionEntity>()
        var sessionIndex = 1
        val graceInterval = if (studyDaysPerWeek <= 5) 5 else 6

        for (item in rawCurriculum) {
            generatedSessions.add(
                StudySessionEntity(
                    planId = planId,
                    sessionNumber = sessionIndex++,
                    bookReferenceEn = item.bookRefEn,
                    bookReferenceSi = item.bookRefSi,
                    passageTitleEn = item.titleEn,
                    passageTitleSi = item.titleSi,
                    passageExcerptEn = item.excerptEn,
                    passageExcerptSi = item.excerptSi,
                    contextNoteEn = item.contextEn,
                    contextNoteSi = item.contextSi,
                    learningNoteEn = item.learningEn,
                    learningNoteSi = item.learningSi,
                    reflectionPromptsEn = item.reflectionPromptsEn.joinToString("||"),
                    reflectionPromptsSi = item.reflectionPromptsSi.joinToString("||"),
                    thoughtQuestionsEn = item.thoughtQuestionsEn.joinToString("||"),
                    thoughtQuestionsSi = item.thoughtQuestionsSi.joinToString("||"),
                    practicalApplicationEn = item.applicationEn,
                    practicalApplicationSi = item.applicationSi,
                    isCatchUpGraceDay = false,
                    isCompleted = false,
                    estimatedMinutes = minutesPerSession
                )
            )

            // Inject gentle Grace Catch-Up day periodically
            if (sessionIndex % (graceInterval + 1) == 0 && sessionIndex < rawCurriculum.size + 4) {
                generatedSessions.add(
                    createGraceDaySession(planId, sessionIndex++, minutesPerSession)
                )
            }
        }

        val totalSessions = generatedSessions.size
        // Calculate realistic completion date based on study days per week
        val completionDateMillis = calculateEstimatedCompletionDate(
            startDateMillis = startDateMillis,
            totalSessions = totalSessions,
            studyDaysPerWeek = studyDaysPerWeek
        )

        val planEntity = PlanEntity(
            id = planId,
            titleEn = "The Gospel of John: Walking with Jesus",
            titleSi = "යොහන් සුබාරංචිය: යේසුස් වහන්සේ සමඟ ගමන",
            descriptionEn = "A gentle step-by-step walk through Jesus's life, teachings, and compassion, customized for your daily schedule.",
            descriptionSi = "යේසුස් වහන්සේගේ ජීවිතය, ඉගැන්වීම් සහ දයාව පිළිබඳ සරල හා ක්‍රමානුකූල දෛනික අධ්‍යයනයක්.",
            style = planStyle,
            totalSessions = totalSessions,
            currentSessionIndex = 1,
            status = "ACTIVE",
            startDateMillis = startDateMillis,
            estimatedCompletionDateMillis = completionDateMillis,
            minutesPerSession = minutesPerSession,
            studyDaysPerWeek = studyDaysPerWeek
        )

        return PlanWithSessions(planEntity, generatedSessions)
    }

    fun calculateEstimatedCompletionDate(
        startDateMillis: Long,
        totalSessions: Int,
        studyDaysPerWeek: Int
    ): Long {
        val safeStudyDays = studyDaysPerWeek.coerceIn(1, 7)
        val totalCalendarDays = ceil((totalSessions.toDouble() / safeStudyDays) * 7.0).toLong()
        val calendar = Calendar.getInstance().apply {
            timeInMillis = startDateMillis
            add(Calendar.DAY_OF_YEAR, totalCalendarDays.toInt())
        }
        return calendar.timeInMillis
    }

    private fun createGraceDaySession(planId: String, sessionNumber: Int, minutes: Int): StudySessionEntity {
        return StudySessionEntity(
            planId = planId,
            sessionNumber = sessionNumber,
            bookReferenceEn = "Grace & Catch-Up Day",
            bookReferenceSi = "කරුණාවේ සහ විවේක දිනය",
            passageTitleEn = "Rest, Breathe & Reflect",
            passageTitleSi = "සැනසෙන්න, හුස්මක් ගන්න සහ මෙනෙහි කරන්න",
            passageExcerptEn = "\"Come to me, all you who are weary and burdened, and I will give you rest.\" (Matthew 11:28 anchor)",
            passageExcerptSi = "\"වෙහෙසවූවෙනි, බර උසුලන්නෙනි, මා වෙතට එන්න, මම ඔබට සහනය දෙමි.\" (සහනදායක නැංගුරම)",
            contextNoteEn = "In biblical rhythms, rest (Sabbath) is a gift of grace, not a penalty. You are not falling behind. Growth happens in quiet rest as much as in active reading.",
            contextNoteSi = "බයිබලානුකූල චාරිත්‍රයන්ට අනුව, විවේකය යනු දඬුවමක් නොව කරුණාවේ ත්‍යාගයකි. ඔබ කිසිවිටෙකත් පමා වී නැත. ක්‍රියාකාරී කියවීමේදී මෙන්ම නිහඬ විවේකයේදීද ආධ්‍යාත්මික වර්ධනය සිදුවේ.",
            learningNoteEn = "Use today to catch up on any missed session, revisit your favorite journal entry from the past week, or simply sit in gratitude without opening any study notes.",
            learningNoteSi = "පසුගිය දිනවල මඟහැරුණු පාඩමක් නැවත කිරීමට, ඔබ ප්‍රිය කළ සටහනක් නැවත කියවීමට හෝ කිසිවක් නොකර ස්තූතිවන්ත සිතින් විවේක ගැනීමට අද දිනය යොදාගන්න.",
            reflectionPromptsEn = "How does your body and soul feel today?||What is one small blessing you noticed this week?",
            reflectionPromptsSi = "අද ඔබේ සිතට සහ කයට දැනෙන්නේ කුමක්ද?||මෙම සතිය තුළ ඔබ දුටු එක් කුඩා ආශීර්වාදයක් කුමක්ද?",
            thoughtQuestionsEn = "Why do we often feel guilty when we pause?||How can grace reshape your relationship with daily habits?",
            thoughtQuestionsSi = "අප විරාමයක් ගත් විට වරදකාරී හැඟීමක් ඇති වන්නේ ඇයි?||කරුණාව ඔබේ දෛනික පුරුදු සමඟ ඇති සබඳතාව අලුත් කරන්නේ කෙසේද?",
            practicalApplicationEn = "Take a 5-minute silent walk outside or sip a warm drink in quiet gratitude. No pressure, no checklist.",
            practicalApplicationSi = "විනාඩි 5ක් නිහඬව සක්මන් කරන්න හෝ උණුසුම් පානයක් රසවිඳිමින් නිහඬව ස්තූති කරන්න. කිසිදු පීඩනයක් නැත.",
            isCatchUpGraceDay = true,
            isCompleted = false,
            estimatedMinutes = minutes
        )
    }

    private data class CurriculumItem(
        val bookRefEn: String,
        val bookRefSi: String,
        val titleEn: String,
        val titleSi: String,
        val excerptEn: String,
        val excerptSi: String,
        val contextEn: String,
        val contextSi: String,
        val learningEn: String,
        val learningSi: String,
        val reflectionPromptsEn: List<String>,
        val reflectionPromptsSi: List<String>,
        val thoughtQuestionsEn: List<String>,
        val thoughtQuestionsSi: List<String>,
        val applicationEn: String,
        val applicationSi: String
    )

    private fun getJohnCurriculum(): List<CurriculumItem> = listOf(
        CurriculumItem(
            bookRefEn = "John 1:1-18",
            bookRefSi = "යොහන් 1:1-18",
            titleEn = "The Word Became Flesh",
            titleSi = "ජීවමාන වචනය මාංසවත් වීම",
            excerptEn = "In the beginning was the Word, and the Word was with God. In him was life, and that life was the light of all humanity. The light shines in the darkness, and the darkness has not overcome it.",
            excerptSi = "ආරම්භයෙහි වචනය විය, වචනය දෙවියන්වහන්සේ සමඟ විය. උන්වහන්සේ තුළ ජීවනය තිබිණ, ඒ ජීවනය මනුෂ්‍යයන්ගේ ආලෝකය විය. අන්ධකාරය එය මැඩපැවැත්වූයේ නැත.",
            contextEn = "John wrote his gospel to non-Jewish and Jewish seekers alike in Ephesus. He starts not with genealogies, but with 'Logos' — the ancient Greek and Hebrew concept of Divine Wisdom actively walking into human history.",
            contextSi = "යොහන් තම සුබාරංචිය ලිව්වේ සියලු ගවේෂකයන් සඳහාය. ඔහු කතාව අරඹන්නේ වංශාවලියකින් නොව 'ලෝගෝස්' හෙවත් ජීවමාන දිව්‍ය ප්‍රඥාව මනුෂ්‍ය ඉතිහාසයට පැමිණීමෙනි.",
            learningEn = "Notice John's confidence: Darkness cannot overpower light. Even the smallest match dispels a room of pitch black. You don't have to fight darkness; simply welcome the light.",
            learningSi = "අන්ධකාරයට ආලෝකය පරාජය කළ නොහැක. කුඩා ගිනි කූරකට පවා මහත් අන්ධකාරයක් දුරු කළ හැක. ඔබ අඳුර සමඟ සටන් කළ යුතු නැත; ආලෝකය පිළිගැනීම ප්‍රමාණවත්ය.",
            reflectionPromptsEn = listOf(
                "Where in your life does it feel dark or uncertain right now?",
                "How does it feel to know that light always outlasts shadow?"
            ),
            reflectionPromptsSi = listOf(
                "මේ මොහොතේ ඔබේ ජීවිතයේ නොපැහැදිලි හෝ අඳුරු තැනක් තිබේද?",
                "ආලෝකය සැමවිටම ජයගන්නා බව දැනෙන විට ඔබේ සිතට දැනෙන්නේ කුමක්ද?"
            ),
            thoughtQuestionsEn = listOf(
                "What does 'the Word became flesh and lived among us' mean for your everyday life?",
                "Can you approach God today as someone near and approachable?"
            ),
            thoughtQuestionsSi = listOf(
                "'වචනය මාංසවී අප අතර වාසය කළේය' යන්න ඔබේ දෛනික ජීවිතයට අදාළ වන්නේ කෙසේද?",
                "දෙවියන් වහන්සේ ළඟා විය හැකි සමීප මිතුරෙකු ලෙස ඔබට අද දැකිය හැකිද?"
            ),
            applicationEn = "When you feel overwhelmed today, pause for 10 seconds and repeat quietly: 'The light is here, and I am not alone.'",
            applicationSi = "අද ඔබට කලබලයක් දැනෙන විට, තත්පර 10ක් නතර වී: 'ආලෝකය මෙහිය, මා තනිවී නැත' කියා සිතින් මෙනෙහි කරන්න."
        ),
        CurriculumItem(
            bookRefEn = "John 1:19-34",
            bookRefSi = "යොහන් 1:19-34",
            titleEn = "A Voice in the Wilderness",
            titleSi = "පාළුකරයේ හඬක්",
            excerptEn = "John the Baptist declared: 'I am a voice calling in the wilderness: Make straight the way for the Lord.' When he saw Jesus, he said: 'Look, the Lamb of God who takes away the sin of the world!'",
            excerptSi = "යොහන් මෙසේ කීය: 'ස්වාමීන්වහන්සේගේ මාවත සෘජු කරන්නැයි පාළුකරයෙහි හඬනගන හඬ මමය.' යේසුස්වහන්සේ දැක ඔහු කීවේ: 'මෙන්න, ලෝකයේ පාපය දුරලන දෙවියන්වහන්සේගේ බැටළු පැටවාණෝ!'",
            contextEn = "Religious leaders pressed John about his identity. John was content being merely 'a voice pointing away from himself.' He felt no need to inflate his importance.",
            contextSi = "ආගමික නායකයන් යොහන්ගෙන් ඔහුගේ තනතුර විමසූ විට, ඔහු තමන්ව හඳුන්වා දුන්නේ අන් අයව ආලෝකය වෙත යොමු කරන හඬක් ලෙස පමණි. තමා ගැන පුරසාරම් දෙඩීමට ඔහුට වුවමනාවක් නොවීය.",
            learningEn = "Humility is not thinking less of yourself; it is thinking of yourself less often. Finding your peace comes from knowing whose you are, not performing for applause.",
            learningSi = "නිහතමානිකම යනු තමා අවතක්සේරු කිරීම නොවේ; තමා ගැන පමණක් සිතීමෙන් මිදීමයි. අන් අයගේ ප්‍රශංසාව මත නොව සැබෑ අනන්‍යතාවය මත පිහිටීම සැනසීම ගෙනදෙයි.",
            reflectionPromptsEn = listOf(
                "Do you feel pressured to prove yourself to people around you?",
                "What burdens can you lay down knowing God already accepts you?"
            ),
            reflectionPromptsSi = listOf(
                "ඔබ අවට සිටින අයට ඔබේ වටිනාකම ඔප්පු කිරීමට ඔබට පීඩනයක් දැනෙනවාද?",
                "දෙවියන් වහන්සේ ඔබව පිළිගන්නා බව දැනීමෙන් ඔබට නිදහස් කළ හැකි බර කුමක්ද?"
            ),
            thoughtQuestionsEn = listOf(
                "How does John's description of Jesus as 'gentle Lamb' contrast with angry depictions of religion?",
                "What would it look like to be a gentle voice of hope in your workplace or home?"
            ),
            thoughtQuestionsSi = listOf(
                "යේසුස් වහන්සේ 'මෘදු බැටළු පැටවාණන්' ලෙස විස්තර කිරීම කෝපාවිෂ්ට ආගමික මතයන්ගෙන් වෙනස් වන්නේ කෙසේද?",
                "ඔබේ නිවසේ හෝ රැකියා ස්ථානයේ බලාපොරොත්තුවේ මෘදු හඬක් වීමට ඔබට හැක්කේ කෙසේද?"
            ),
            applicationEn = "Offer sincere appreciation or an encouraging text to someone today without expecting anything back.",
            applicationSi = "කිසිදු පෙරළා බලාපොරොත්තුවකින් තොරව අද යමෙකුට අවංක ප්‍රශංසාවක් හෝ දිරිගැන්වීමේ පණිවිඩයක් එවන්න."
        ),
        CurriculumItem(
            bookRefEn = "John 1:35-51",
            bookRefSi = "යොහන් 1:35-51",
            titleEn = "Come and See",
            titleSi = "ඇවිත් බලන්න",
            excerptEn = "Jesus turned and saw them following and asked, 'What do you want?' They asked, 'Where are you staying?' 'Come,' he replied, 'and you will see.' Later, Philip told Nathanael: 'Come and see.'",
            excerptSi = "යේසුස්වහන්සේ ඔවුන් තමන් පස්සේ එනවා දැක, 'ඔබ කුමක් සොයනවාද?' යි ඇසූසේක. ඔවුහු 'ඔබ කොහේ නවාතැන් ගෙන සිටිනවාද?' ඇසූහ. 'ඇවිත් බලන්න' යි උන්වහන්සේ කීසේක.",
            contextEn = "Notice Jesus's very first spoken words in John's gospel: 'What do you want?' It was a compassionate invitation to genuine friendship, not an interrogation or test.",
            contextSi = "යොහන්ගේ සුබාරංචියේ යේසුස් වහන්සේගේ පළමු වචනය වූයේ 'ඔබ කුමක් සොයනවාද?' යන්නයි. එය විභාගයක් හෝ චෝදනාවක් නොව අවංක මිත්‍රත්වයකට කළ ඇරයුමකි.",
            learningEn = "Faith often begins with simple curiosity: 'Come and see.' You don't need all answers before taking a first step. Jesus invites honest explorers with open arms.",
            learningSi = "ඇදහිල්ල බොහෝවිට ඇරඹෙන්නේ කුතුහලයෙනි: 'ඇවිත් බලන්න'. පළමු පියවර තැබීමට පෙර සියලු ප්‍රශ්නවලට පිළිතුරු දැනසිටීම අවශ්‍ය නොවේ.",
            reflectionPromptsEn = listOf(
                "If Jesus asked you today, 'What do you really want?', what would your heart answer?",
                "Are you open to discovering peace in small, unexpected places?"
            ),
            reflectionPromptsSi = listOf(
                "යේසුස් වහන්සේ අද ඔබෙන් 'ඔබේ හදවත සැබවින්ම සොයන්නේ කුමක්ද?' ඇසුවොත් ඔබ පවසන්නේ කුමක්ද?",
                "කුඩා නොසිතූ මොහොතක සැනසීම සොයාගැනීමට ඔබ විවෘතද?"
            ),
            thoughtQuestionsEn = listOf(
                "Why is 'Come and see' less intimidating than religious arguments?",
                "What made Nathanael change his skepticism into wonder?"
            ),
            thoughtQuestionsSi = listOf(
                "'ඇවිත් බලන්න' යන ආරාධනාව වාද විවාදවලට වඩා මිත්‍රශීලී වන්නේ ඇයි?",
                "නතානියෙල්ගේ සැකය පුදුමයක් බවට පත් කළේ කුමක්ද?"
            ),
            applicationEn = "Write down one honest desire or worry in your journal today as an open conversation.",
            applicationSi = "ඔබේ හදවතේ ඇති එක් සැබෑ බලාපොරොත්තුවක් හෝ කනස්සල්ලක් අද සටහනේ ලියන්න."
        ),
        CurriculumItem(
            bookRefEn = "John 2:1-12",
            bookRefSi = "යොහන් 2:1-12",
            titleEn = "Joy at the Wedding in Cana",
            titleSi = "කානාහි මංගල්‍යයේ ප්‍රීතිය",
            excerptEn = "Jesus did this, the first of his signs, in Cana of Galilee. He revealed his glory, transforming ordinary water into the sweetest wine, blessing a family's joy.",
            excerptSi = "යේසුස්වහන්සේ ගලීලයේ කානාහිදී තම හාස්කම්වල පළමුවැන්න කළසේක. සාමාන්‍ය වතුර ප්‍රණීත මිදි යුෂ බවට පත්කරමින් උන්වහන්සේ මංගල ප්‍රීතිය ආශීර්වාද කළසේක.",
            contextEn = "Running out of wine at a Middle Eastern wedding was a social disaster bringing deep shame to the host family. Jesus cared enough about their social dignity to perform his first public sign.",
            contextSi = "මංගල්‍යයක බීම අවසන් වීම ඒ පවුලට මහත් සමාජ ලැජ්ජාවක් විය. යේසුස් වහන්සේ තම පළමු හාස්කම කළේ එම පවුලේ ගෞරවය සහ සතුට රැකගැනීමටය.",
            learningEn = "God cares about our celebrations, social relationships, and human moments — not just religious ceremonies. God takes our plain, everyday 'water' and turns it into joy.",
            learningSi = "දෙවියන් වහන්සේ අපගේ ආගමික වතාවත් ගැන පමණක් නොව, අපගේ පවුලේ සතුට, සබඳතා සහ දෛනික ජීවිතය ගැනද ගැඹුරින් සලකයි. සාමාන්‍ය දෙය අගනා දෙයක් බවට පත් කිරීමට උන්වහන්සේට හැක.",
            reflectionPromptsEn = listOf(
                "Where in your life do you feel things have run dry or empty?",
                "Can you invite God into your everyday concerns and simple joys?"
            ),
            reflectionPromptsSi = listOf(
                "ඔබේ ජීවිතයේ හිස්බවක් හෝ වියළි බවක් දැනෙන තැනක් තිබේද?",
                "ඔබේ දෛනික සතුටට සහ කනස්සල්ලට දෙවියන් වහන්සේට ඇරයුම් කළ හැකිද?"
            ),
            thoughtQuestionsEn = listOf(
                "What does this story show about Jesus's view of human celebration?",
                "How does Mary's advice, 'Do whatever he tells you,' speak to quiet trust?"
            ),
            thoughtQuestionsSi = listOf(
                "මනුෂ්‍ය ප්‍රීතිය පිළිබඳ යේසුස් වහන්සේගේ ආකල්පය මෙයින් පැහැදිලි වන්නේ කෙසේද?",
                "'උන්වහන්සේ ඔබට කියන ඕනෑම දෙයක් කරන්න' යන මරියාගේ උපදෙස නිහඬ විශ්වාසය ගැන පවසන්නේ කුමක්ද?"
            ),
            applicationEn = "Celebrate a small everyday blessing today: enjoy a meal, smile with a loved one, or share laughter.",
            applicationSi = "අද දවසේ එක් කුඩා සතුටක් සමරන්න: රසවත් ආහාරයක් භුක්ති විඳින්න හෝ මිතුරෙකු සමඟ සිනහවක් බෙදාගන්න."
        ),
        CurriculumItem(
            bookRefEn = "John 2:13-25",
            bookRefSi = "යොහන් 2:13-25",
            titleEn = "Clearing the Courtyard",
            titleSi = "දේව මාලිගාවේ පිට්ටනිය පිරිසිදු කිරීම",
            excerptEn = "In the temple courts he found people selling cattle and exchanging money where prayer was meant to happen. He cleared the space, saying: 'Stop turning my Father’s house into a market!'",
            excerptSi = "යාච්ඤාව සඳහා වෙන්වූ මාලිගාවේ වෙළඳාම් කරමින් මුදල් මාරු කරන්නන් දැක උන්වහන්සේ එම ස්ථානය පිරිසිදු කරමින්: 'මගේ පියාණන්ගේ නිවස වෙළඳපොළක් නොකරන්න' යි වදාළසේක.",
            contextEn = "The temple had an 'Outer Court of the Gentiles' — the only place foreigners, seekers, and poor travelers could pray. Corrupt vendors had filled this sacred space with noise and greed.",
            contextSi = "මාලිගාවේ පිටත මළුව වෙන්ව තිබුණේ දුප්පතුන්ට සහ විදේශීය ගවේෂකයන්ට යාච්ඤා කිරීමටයි. කපටි වෙළෙන්දන් එම ශුද්ධ ස්ථානය ඝෝෂාවෙන් හා තණ්හාවෙන් පුරවා තිබිණ.",
            learningEn = "Jesus stands up for the vulnerable and removes barriers between people and peace. Righteous passion clears out whatever clutters our spiritual freedom.",
            learningSi = "යේසුස් වහන්සේ සාමාන්‍ය ජනයා සහ දෙවියන් අතර ඇති බාධක ඉවත් කිරීමට ක්‍රියා කළ සේක. අපගේ සිත අවුල් කරන අනවශ්‍ය දේවල් ඉවත් කිරීමට උන්වහන්සේ මඟ පෙන්වයි.",
            reflectionPromptsEn = listOf(
                "What noise, clutter, or distractions are crowding out peace in your mind today?",
                "What can you gently remove or turn off to create sacred quiet?"
            ),
            reflectionPromptsSi = listOf(
                "අද ඔබේ සිතේ සැනසීම අවුල් කරන අනවශ්‍ය ඝෝෂාව හෝ අවධානය වෙනතකට යොමු කරන දේ කුමක්ද?",
                "නිහඬ සැනසීමක් ලබාගැනීමට ඔබට ඉවත් කළ හැකි දේ කුමක්ද?"
            ),
            thoughtQuestionsEn = listOf(
                "Why was Jesus passionate about protecting the place of quiet prayer?",
                "How is your heart today like a temple where quiet reflection belongs?"
            ),
            thoughtQuestionsSi = listOf(
                "නිහඬ යාච්ඤාවේ ස්ථානය රැකගැනීමට යේසුස් වහන්සේ තුළ මෙතරම් උනන්දුවක් තිබුණේ ඇයි?",
                "ඔබේ හදවත සන්සුන් භාවනාවට සුදුසු ශුද්ධ ස්ථානයක් කරගන්නේ කෙසේද?"
            ),
            applicationEn = "Take a 10-minute digital fast today: turn off notifications and enjoy pure quiet stillness.",
            applicationSi = "අද විනාඩි 10ක් දුරකථනයේ ශබ්දය නවතා සම්පූර්ණ නිශ්ශබ්දතාවයකින් ගත කරන්න."
        ),
        CurriculumItem(
            bookRefEn = "John 3:1-21",
            bookRefSi = "යොහන් 3:1-21",
            titleEn = "A Conversation Under the Stars",
            titleSi = "තාරකා යට පැවති සංවාදයක්",
            excerptEn = "Jesus told Nicodemus: 'For God so loved the world that he gave his one and only Son, that whoever believes in him shall not perish but have eternal life. For God did not send his Son into the world to condemn the world, but to save it through him.'",
            excerptSi = "යේසුස්වහන්සේ නිකොදේමුස්ට මෙසේ වදාළසේක: 'දෙවියන්වහන්සේ ලෝකයා හෙළාදැකීමට නොව, උන්වහන්සේ කරණකොටගෙන ලෝකයා ගැලවෙන පිණිස තම ඒකජාතක පුත්‍රයාණන් දුන්සේක. මන්ද උන්වහන්සේ ලෝකයට එතරම් ප්‍රේම කළසේක.'",
            contextEn = "Nicodemus was an intellectual religious leader who came to Jesus by night. Jesus didn't lecture him; he spoke tenderly about rebirth of the heart like a gentle wind.",
            contextSi = "නිකොදේමුස් යනු විද්වත් ආගමික නායකයෙකි. ඔහු රාත්‍රියේ රහසින් පැමිණි විට යේසුස් වහන්සේ ඔහුට දොස් නොකියා හදවතේ අලුත් වීම සුළඟක් මෙන් සිදුවන අයුරු පහදා දුන්සේක.",
            learningEn = "Notice the core motive: LOVE. 'God did not send his Son to condemn, but to heal and restore.' If faith has ever felt condemning to you, remember John 3:17.",
            learningSi = "මූලික චේතනාව වන්නේ ප්‍රේමයයි. 'දෙවියන් වහන්සේ ආවේ හෙළාදැකීමට නොව සුවපත් කිරීමටය.' ඔබට ආගමෙන් කිසිදා වරදකාරී බියක් දැනී ඇත්නම්, මෙම ප්‍රේමය සිහි කරන්න.",
            reflectionPromptsEn = listOf(
                "Have you ever felt judged or condemned by religious voices?",
                "How does it feel to hear that God's posture toward you is love and rescue, not condemnation?"
            ),
            reflectionPromptsSi = listOf(
                "ආගමික අදහස් නිසා ඔබ කිසිදා හෙළාදැකීමකට ලක්ව ඇති බවක් දැනී තිබේද?",
                "දෙවියන් වහන්සේ ඔබ දෙස බලන්නේ චෝදනාවෙන් නොව ප්‍රේමයෙන් බව ඇසෙන විට ඔබට දැනෙන්නේ කුමක්ද?"
            ),
            thoughtQuestionsEn = listOf(
                "What does starting fresh or 'being born anew' look like in your life right now?",
                "How does the analogy of the wind describe the gentle mystery of inner renewal?"
            ),
            thoughtQuestionsSi = listOf(
                "ජීවිතය අලුතින් ඇරඹීම යන්න මේ මොහොතේ ඔබට අදාළ වන්නේ කෙසේද?",
                "සුළඟේ උපමාව මඟින් අභ්‍යන්තර සන්සුන් වෙනස පැහැදිලි කරන්නේ කෙසේද?"
            ),
            applicationEn = "Release any self-critical thought today with this sentence: 'I am loved, I am learning, and grace is renewing me.'",
            applicationSi = "අද ඔබ ගැනම ඇතිවන චෝදනා සිතුවිලි ඉවත් කරමින්: 'මා ප්‍රේමයට පාත්‍රව ඇත, මම ඉගෙන ගනිමින් සිටිමි, කරුණාව මා අලුත් කරයි' යැයි සිතන්න."
        ),
        CurriculumItem(
            bookRefEn = "John 4:1-26",
            bookRefSi = "යොහන් 4:1-26",
            titleEn = "Living Water at the Well",
            titleSi = "ළිඳ අසල ජීවමාන දිය උල්පත",
            excerptEn = "Jesus said to her, 'Everyone who drinks this water will be thirsty again, but whoever drinks the water I give them will never thirst. Indeed, the water I give them will become in them a spring of water welling up to eternal life.'",
            excerptSi = "යේසුස්වහන්සේ ඈට පිළිතුරු දෙමින්: 'මේ වතුර බොන සියල්ලන් නැවත පිපාසිත වනු ඇත. නමුත් මා දෙන වතුර බොන කිසිවෙක් කිසිදා පිපාසිත නොවන්නේය. එය ඔහු තුළ සදාකාල ජීවනය පිණිස උතුරා යන උල්පතක් වන්නේය.'",
            contextEn = "Strict cultural taboos forbade Jewish men from talking to Samaritan women in public. Jesus deliberately bypassed ethnic, gender, and social barriers to sit and speak at midday with an isolated woman.",
            contextSi = "යුදෙව්වන් සමාරියවරුන් සමඟ කතා නොකළ අතර, තනිවූ කාන්තාවක් සමඟ කතා කිරීම තහනම් විය. යේසුස් වහන්සේ ඒ සියලු සමාජ හා ජාති බාධක බිඳදමා මධ්‍යාහ්නයේ ඈ සමඟ සුහදව කතා කළ සේක.",
            learningEn = "We all chase temporary things to satisfy deep longings (approval, status, escape). Jesus offers an inner spring of unconditional worth that never runs dry.",
            learningSi = "අප සියල්ලෝම සැනසීම සොයා තාවකාලික දේ පසුපස හඹා යමු. නමුත් යේසුස් වහන්සේ ලබාදෙන්නේ කිසිදා සිඳී නොයන අභ්‍යන්තර සැනසීමේ දිය උල්පතකි.",
            reflectionPromptsEn = listOf(
                "What are you turning to when you feel emotionally exhausted or lonely?",
                "Can you imagine receiving a kindness that knows all about you and still loves you?"
            ),
            reflectionPromptsSi = listOf(
                "ඔබට මහන්සියක් හෝ තනිකමක් දැනෙන විට ඔබ සැනසීම සොයන්නේ කොතැනින්ද?",
                "ඔබ ගැන සියල්ල දැන සිටියත් ඔබට කොන්දේසි විරහිතව ප්‍රේම කරන කරුණාවක් ගැන ඔබට සිතිය හැකිද?"
            ),
            thoughtQuestionsEn = listOf(
                "How does Jesus treating the Samaritan woman with honor challenge our social prejudices?",
                "What does 'worship in spirit and truth' mean for someone praying at home?"
            ),
            thoughtQuestionsSi = listOf(
                "යේසුස් වහන්සේ සමාරීය කාන්තාවට ගෞරවයෙන් සැලකීම අපගේ සමාජ ආකල්පවලට අභියෝග කරන්නේ කෙසේද?",
                "නිවසේ සිට යාච්ඤා කරන කෙනෙකුට 'ආත්මයෙන් හා සත්‍යයෙන් නමස්කාර කිරීම' අදාළ වන්නේ කෙසේද?"
            ),
            applicationEn = "Reach out or say something kind to someone who might feel left out or overlooked today.",
            applicationSi = "අද සමාජයේ හෝ වැඩබිමේ කොන්වී සිටින හෝ තනිවූ කෙනෙකුට කාරුණික වචනයක් කියන්න."
        ),
        CurriculumItem(
            bookRefEn = "John 5:1-18",
            bookRefSi = "යොහන් 5:1-18",
            titleEn = "Do You Want to Get Well?",
            titleSi = "සුවපත් වීමට ඔබ කැමතිද?",
            excerptEn = "Near the pool called Bethesda lay an invalid who had been ill for thirty-eight years. When Jesus saw him lying there, he asked him, 'Do you want to get well?' Then Jesus said, 'Get up! Pick up your mat and walk.'",
            excerptSi = "බෙතෙස්දා නම් පොකුණ අසල අවුරුදු තිස්අටක් රෝගීව සිටි මිනිසෙක් විය. යේසුස්වහන්සේ ඔහු දැක, 'සුවපත් වීමට ඔබ කැමතිද?' යි ඇසූසේක. උන්වහන්සේ ඔහුට: 'නැගිටින්න, ඔබේ පැදුර රැගෙන ඇවිදින්න' යි වදාළසේක.",
            contextEn = "Thirty-eight years is a lifetime. The man had become accustomed to waiting and feeling powerless. Jesus gently nudged his will: healing begins with the desire for hope.",
            contextSi = "අවුරුදු 38ක් යනු දීර්ඝ කාලයකි. බලාපොරොත්තු සුන්වී බලා සිටීම ඔහුගේ පුරුද්දක් වී තිබිණ. යේසුස් වහන්සේ ඔහුගේ බලාපොරොත්තුව යළි අවදි කරමින් නැගී සිටීමට මඟ පෙන්වූ සේක.",
            learningEn = "Sometimes we get comfortable in our discouragement because trying again feels risky. Jesus meets us with gentle power: take one small step today.",
            learningSi = "සමහරවිට නැවත උත්සාහ කිරීමට බිය නිසා අප අපගේ අසරණකමට පුරුදු වෙමු. යේසුස් වහන්සේ අපව හමුවන්නේ ශක්තියෙනි: අද එක් කුඩා පියවරක් තබන්න.",
            reflectionPromptsEn = listOf(
                "Is there an old disappointment or rut you have given up on?",
                "What would 'picking up your mat' look like today?"
            ),
            reflectionPromptsSi = listOf(
                "ඔබ අතහැර දැමූ පැරණි බලාපොරොත්තු සුන්වීමක් ඔබේ සිතේ තිබේද?",
                "අද දවසේ 'නැගී සිට ඉදිරියට යාම' යන්නෙන් අදහස් වන්නේ කුමක්ද?"
            ),
            thoughtQuestionsEn = listOf(
                "Why did Jesus ask a paralyzed man if he wanted to be healed?",
                "How does hope require courage?"
            ),
            thoughtQuestionsSi = listOf(
                "සුවපත් වීමට කැමතිදැයි රෝගියාගෙන් යේසුස් වහන්සේ ඇසුවේ ඇයි?",
                "බලාපොරොත්තුව තබාගැනීමට ධෛර්යය අවශ්‍ය වන්නේ ඇයි?"
            ),
            applicationEn = "Take one small constructive action today that you have been putting off for too long.",
            applicationSi = "කාලයක් තිස්සේ කල්දමමින් සිටි එක් කුඩා යහපත් කාර්යයක් අද ඉටු කරන්න."
        ),
        CurriculumItem(
            bookRefEn = "John 6:1-15",
            bookRefSi = "යොහන් 6:1-15",
            titleEn = "A Small Gift Multiplied",
            titleSi = "කුඩා දායකත්වය ආශීර්වාද වීම",
            excerptEn = "Andrew said, 'Here is a boy with five small barley loaves and two small fish, but how far will they go among so many?' Jesus took the loaves, gave thanks, and distributed to everyone as much as they wanted.",
            excerptSi = "අන්දෘ පැවසුවේ: 'බාර්ලි රොටි පහක් සහ කුඩා මසුන් දෙදෙනෙකු ඇති ළමයෙක් මෙහි සිටී, නමුත් මෙතරම් සෙනඟකට ඒ කුමක්ද?' යේසුස්වහන්සේ රොටි ගෙන, ස්තූතිකොට, සැමටම ප්‍රමාණවත් වන සේ බෙදා දුන්සේක.",
            contextEn = "Five thousand hungry people in a remote field. The disciples saw impossibility; a young boy simply gave what he had. Jesus did not ask for a banquet, only an open hand.",
            contextSi = "කාන්තාර බිමක කුසගින්නේ සිටි දහස් ගණන් ජනයා. ගෝලයන් දුටුවේ කළ නොහැකි දෙයකි; නමුත් කුඩා ළමයෙක් තමා සතු කුඩා ආහාරය ලබාදුන්නේය. දෙවියන් වහන්සේ බලාපොරොත්තු වන්නේ අප සතු කුඩා කැපවීමයි.",
            learningEn = "You don't need huge talent, boundless time, or great wealth to make a difference. Offer your small 5 minutes or small kind gesture; grace multiplies what you give.",
            learningSi = "යහපතක් කිරීමට ඔබට විශාල ධනයක් හෝ දැනුමක් අවශ්‍ය නැත. ඔබ සතු විනාඩි 5 හෝ කුඩා කාරුණික ක්‍රියාව දෙන්න; කරුණාව එය මහත් ආශීර්වාදයක් කරනු ඇත.",
            reflectionPromptsEn = listOf(
                "Do you ever feel your daily time or efforts are too small to count?",
                "How does it feel to know that small, consistent steps are enough?"
            ),
            reflectionPromptsSi = listOf(
                "ඔබේ දෛනික උත්සාහය හෝ කාලය ඉතා කුඩා යැයි ඔබට සිතී තිබේද?",
                "කුඩා අඛණ්ඩ පියවරයන් පවා ප්‍රමාණවත් බව දැනෙන විට ඔබට සැනසීමක් දැනෙනවාද?"
            ),
            thoughtQuestionsEn = listOf(
                "Why was thanksgiving Jesus's first action before the bread was multiplied?",
                "What can you give thanks for right now before seeing tomorrow's answers?"
            ),
            thoughtQuestionsSi = listOf(
                "ආහාර බෙදීමට පෙර යේසුස් වහන්සේ ස්තූති කළේ ඇයි?",
                "හෙට දවසේ පිළිතුරු ලැබීමට පෙර අද ඔබට ස්තූති කළ හැක්කේ කුමක් ගැනද?"
            ),
            applicationEn = "Say a genuine thank you for your food today, acknowledging the hands that prepared it.",
            applicationSi = "අද ඔබේ ආහාරය වෙනුවෙන් සහ එය පිළියෙළ කළ දෑත් වෙනුවෙන් අවංකව ස්තූතිවන්ත වන්න."
        ),
        CurriculumItem(
            bookRefEn = "John 6:16-24",
            bookRefSi = "යොහන් 6:16-24",
            titleEn = "Peace in the Storm",
            titleSi = "කුණාටුව මැද සාමය",
            excerptEn = "A strong wind was blowing and the waters grew rough. Then they saw Jesus approaching the boat, walking on the water, and they were frightened. But he said to them, 'It is I; do not be afraid.'",
            excerptSi = "තද සුළඟක් හමා රළ ගොඩගැසිණ. යේසුස්වහන්සේ ජලය මත ඇවිදිමින් බෝට්ටුව දෙසට එනවා දැක ඔවුහු බිය වූහ. නමුත් උන්වහන්සේ: 'මේ මමය, බිය නොවන්න' යි ඔවුන්ට වදාළසේක.",
            contextEn = "The Sea of Galilee is prone to sudden, violent nocturnal squalls. The disciples were rowing against the wind in pitch dark at 3 AM. Jesus walked toward them directly in the storm.",
            contextSi = "ගලීල මුහුදේ හදිසි කුණාටු ඇතිවීම සාමාන්‍ය දෙයකි. ගෝලයන් මධ්‍යම රාත්‍රියේ දැඩි වෙහෙසකින් බෝට්ටුව පදවමින් සිටියහ. යේසුස් වහන්සේ කුණාටුව මැදින් ඔවුන් වෙත පැමිණි සේක.",
            learningEn = "Jesus does not always stop the storm instantly; he steps into the storm with you. His presence brings calm even before the wind subsides.",
            learningSi = "යේසුස් වහන්සේ සැමවිටම කුණාටුව එකවර නවත්වන්නේ නැත; උන්වහන්සේ කුණාටුව මැදට පැමිණ අප සමඟ සිටියි. සුළඟ නතර වීමටත් පෙර උන්වහන්සේගේ පැමිණීම සාමය ගෙනදෙයි.",
            reflectionPromptsEn = listOf(
                "What turbulence or anxiety are you navigating in your heart right now?",
                "Can you hear the whisper: 'It is I; do not be afraid'?"
            ),
            reflectionPromptsSi = listOf(
                "මේ මොහොතේ ඔබේ හදවතේ ඇති නොසන්සුන් කුණාටුව කුමක්ද?",
                "'මේ මමය, බිය නොවන්න' යන මෘදු හඬ ඔබට ඇසෙනවාද?"
            ),
            thoughtQuestionsEn = listOf(
                "Why do we often fear help when it arrives in unfamiliar ways?",
                "How does peace differ from the absence of trouble?"
            ),
            thoughtQuestionsSi = listOf(
                "නොසිතූ ආකාරයෙන් උදව් ලැබෙන විට අප මුලින් බිය වන්නේ ඇයි?",
                "කරදර නැතිවීම සහ සැබෑ අභ්‍යන්තර සාමය අතර වෙනස කුමක්ද?"
            ),
            applicationEn = "Whenever worry creeps in today, breathe deeply and whisper: 'Peace in the storm. I am held.'",
            applicationSi = "අද කනස්සල්ලක් දැනෙන ඕනෑම මොහොතක, ගැඹුරු හුස්මක් ගෙන: 'කුණාටුව මැද මට සාමය ඇත' යැයි සිතන්න."
        )
    )
}

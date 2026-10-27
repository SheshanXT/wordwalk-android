package com.example.localization

/**
 * Complete bilingual localization definitions for WordWalk (English and Sinhala - සිංහල).
 * All UI labels, onboarding options, notifications, empty states, and validations are mapped here.
 */
object LocaleStrings {
    const val LANG_EN = "en"
    const val LANG_SI = "si"

    fun get(key: String, lang: String): String {
        val map = if (lang == LANG_SI) sinhalaStrings else englishStrings
        return map[key] ?: englishStrings[key] ?: key
    }

    private val englishStrings = mapOf(
        // App identity
        "app_title" to "WordWalk",
        "app_tagline" to "Your gentle daily walk with Scripture",
        "app_subtitle" to "Personalized Bible learning & daily spiritual habit",

        // General UI & Navigation
        "nav_home" to "Today",
        "nav_study" to "Walk",
        "nav_plan" to "Plan",
        "nav_progress" to "Progress",
        "nav_settings" to "Settings",
        "btn_continue" to "Continue",
        "btn_back" to "Back",
        "btn_next" to "Next",
        "btn_save" to "Save",
        "btn_complete" to "Complete Session",
        "btn_completed" to "Completed",
        "btn_edit" to "Edit Plan",
        "btn_pause" to "Pause Plan",
        "btn_resume" to "Resume Plan",
        "btn_close" to "Close",
        "btn_cancel" to "Cancel",
        "btn_apply" to "Apply Changes",
        "btn_start_walk" to "Begin Today's Walk",
        "btn_review" to "Review Session",
        "btn_grace_catchup" to "Take a Grace Catch-Up Day",

        // Language Switcher
        "lang_switch_title" to "Language / භාෂාව",
        "lang_english" to "English",
        "lang_sinhala" to "සිංහල (Sinhala)",

        // Onboarding Questionnaire
        "onboarding_welcome_title" to "Welcome to WordWalk",
        "onboarding_welcome_desc" to "A peaceful, pressure-free way to explore the Bible, build daily consistency, and find quiet reflection.",
        "onboarding_step_of" to "Step %d of %d",
        "onboarding_resume_notice" to "Welcome back! You can resume from where you left off.",
        "onboarding_btn_start" to "Get Started",
        "onboarding_btn_resume" to "Resume Setup",

        // Question 1: Language
        "q_lang_title" to "Choose your preferred language",
        "q_lang_desc" to "You can switch anytime in settings.",

        // Question 2: Experience
        "q_exp_title" to "What is your Bible experience?",
        "q_exp_desc" to "We adapt the explanations and context to meet you right where you are.",
        "exp_beginner" to "Complete Beginner",
        "exp_beginner_desc" to "New to the Bible, seeking clear guidance without complex terms",
        "exp_explored" to "Explored a Little",
        "exp_explored_desc" to "Familiar with some stories, but want structured understanding",
        "exp_returning" to "Returning After a Break",
        "exp_returning_desc" to "Looking for a fresh, gentle restart to reconnect",
        "exp_consistent" to "Seeking Consistency",
        "exp_consistent_desc" to "Read before, but struggle with building a steady daily habit",

        // Question 3: Goals
        "q_goals_title" to "What is your primary spiritual goal?",
        "q_goals_desc" to "Select what you hope to experience during this journey.",
        "goal_habit" to "Build a peaceful daily habit",
        "goal_jesus" to "Understand Jesus's life and words",
        "goal_peace" to "Find daily calm, hope, and strength",
        "goal_apply" to "Learn practical application for daily life",
        "goal_overcome" to "Overcome intimidation with scripture",

        // Question 4: Time per session
        "q_time_title" to "How much time can you dedicate daily?",
        "q_time_desc" to "Even 5 intentional minutes can transform your day. Consistency beats duration.",
        "time_5min" to "5 Minutes (Quick & Focused)",
        "time_10min" to "10 Minutes (Recommended for beginners)",
        "time_15min" to "15 Minutes (Deeper reflection)",
        "time_20min" to "20 Minutes (Comprehensive study)",

        // Question 5: Study Days
        "q_days_title" to "How many days per week would you like to study?",
        "q_days_desc" to "WordWalk includes built-in grace days so you never feel guilty or behind.",
        "days_5" to "5 Days a week (Includes 2 Grace/Catch-up days)",
        "days_6" to "6 Days a week (Includes 1 Grace day)",
        "days_7" to "7 Days a week (Daily walk)",

        // Question 6: Reminder time
        "q_reminder_title" to "When would you like your daily reminder?",
        "q_reminder_desc" to "A gentle prompt helps anchor your new habit.",
        "rem_morning" to "Morning (07:00 AM) — Start the day with peace",
        "rem_midday" to "Midday (12:30 PM) — A pause in the busy day",
        "rem_evening" to "Evening (08:30 PM) — End the day with quiet reflection",
        "rem_custom" to "Custom Time",

        // Question 7: Biggest challenge
        "q_challenge_title" to "What has been your biggest hurdle?",
        "q_challenge_desc" to "We will offer tips and gentle encouragement specifically for this.",
        "chal_time" to "Not enough time in busy schedule",
        "chal_context" to "Difficult to understand ancient language or context",
        "chal_consistency" to "Hard to stay consistent and maintain momentum",
        "chal_start" to "Not knowing where to start or what to read",

        // Question 8: Plan Style
        "q_style_title" to "Choose your starting plan style",
        "q_style_desc" to "For beginners, we warmly recommend the Gospel of John — the heart of Jesus's story.",
        "style_john" to "Gospel of John: Life & Teachings (Recommended)",
        "style_john_desc" to "Discover who Jesus is through personal encounters, signs, and love.",
        "style_wisdom" to "Gentle Start: Psalms & Wisdom",
        "style_wisdom_desc" to "Short poetic reflections of comfort, praise, and guidance.",
        "style_foundations" to "Foundations: Core Teachings",
        "style_foundations_desc" to "Essential principles of faith, love, and community living.",

        // Plan Generation Screen
        "plan_creating" to "Crafting your personal WordWalk plan...",
        "plan_ready_title" to "Your Personalized Plan is Ready!",
        "plan_title_john" to "The Gospel of John: Step-by-Step",
        "plan_summary" to "%d sessions • %d minutes each • Includes Grace Days",
        "plan_start_date" to "Starting Date: %s",
        "plan_est_completion" to "Estimated Completion: %s",
        "plan_grace_note" to "Built with compassion: missed days automatically roll forward without shaming or broken streaks.",
        "plan_sessions_label" to "Plan Curriculum",

        // Dashboard
        "dash_greeting_morning" to "Good morning",
        "dash_greeting_afternoon" to "Good afternoon",
        "dash_greeting_evening" to "Good evening",
        "dash_welcome_sub" to "Take a breath. Grace is here for your journey today.",
        "dash_today_card_title" to "Today's Walk",
        "dash_session_indicator" to "Session %d of %d",
        "dash_est_time" to "%d min walk",
        "dash_streak_label" to "Day Streak",
        "dash_completed_sessions" to "Completed",
        "dash_plan_progress" to "Plan Progress",
        "dash_missed_banner_title" to "Missed yesterday? No worries!",
        "dash_missed_banner_desc" to "Spiritual growth is a journey, not a rigid checklist. Take a grace day or resume right now.",
        "dash_verse_anchor_title" to "Daily Anchor Thought",

        // Daily Study Screen
        "study_tab_context" to "1. Context",
        "study_tab_reflection" to "2. Reflection",
        "study_tab_notes" to "3. Insights",
        "study_tab_questions" to "4. Questions",
        "study_tab_application" to "5. Application",
        "study_tab_journal" to "6. Journal",
        "study_passage_heading" to "Passage Reference & Context",
        "study_disclaimer" to "Study note: Bible text references and original learning commentary. Refer to your favorite translation alongside this guide.",
        "study_key_theme" to "Core Message",
        "study_context_heading" to "Historical & Everyday Context",
        "study_reflection_heading" to "Reflection Prompts",
        "study_learning_notes_heading" to "Key Beginner Insights",
        "study_questions_heading" to "Questions to Consider",
        "study_application_heading" to "Today's Practical Walk",
        "study_journal_heading" to "Prayer & Reflection Journal",
        "study_journal_prompt_learned" to "What resonated with me today?",
        "study_journal_prompt_surrender" to "What worry or thought am I letting go of?",
        "study_journal_prompt_prayer" to "My prayer or gratitude today...",
        "study_journal_placeholder" to "Write your thoughts freely. Everything is safely stored on your device.",
        "study_complete_dialog_title" to "Well Done!",
        "study_complete_dialog_desc" to "You have walked with Scripture today. May these words bring peace and clarity to your hours ahead.",
        "study_grace_day_title" to "Grace & Catch-Up Day",
        "study_grace_day_desc" to "Today is a scheduled pause day. Review past reflections, catch up on a missed day, or simply rest in peace.",

        // Progress Screen
        "prog_title" to "Your Spiritual Habit",
        "prog_stats_title" to "Habit Overview",
        "prog_streak" to "%d Days Active",
        "prog_total_completed" to "%d Sessions Finished",
        "prog_minutes_spent" to "%d Total Minutes",
        "prog_milestones_title" to "Milestones & Badges",
        "prog_badge_first" to "First Step",
        "prog_badge_first_desc" to "Completed your first daily walk",
        "prog_badge_3day" to "3-Day Seed",
        "prog_badge_3day_desc" to "Walked 3 consecutive sessions",
        "prog_badge_7day" to "7-Day Habit",
        "prog_badge_7day_desc" to "Reached 7 completed walks",
        "prog_badge_john_start" to "Gospel Explorer",
        "prog_badge_john_start_desc" to "Began the life journey of Jesus",
        "prog_badge_grace" to "Grace Champion",
        "prog_badge_grace_desc" to "Used a grace day with zero shame",
        "prog_past_journals_title" to "Your Reflection Journal",
        "prog_empty_journal" to "Your completed reflections and prayer notes will be gathered here.",

        // Settings Screen
        "settings_title" to "Settings",
        "settings_pref_section" to "Preferences",
        "settings_lang_label" to "App Language",
        "settings_notifications_section" to "Daily Reminders",
        "settings_reminder_toggle" to "Enable Daily Reminder",
        "settings_reminder_time" to "Reminder Time",
        "settings_test_notification" to "Send Test Notification Now",
        "settings_test_notification_sent" to "Test notification sent! Check your notification tray.",
        "settings_plan_section" to "Active Plan",
        "settings_plan_status" to "Plan Status",
        "settings_plan_pace" to "Session Length",
        "settings_backup_section" to "Data & Privacy",
        "settings_export_data" to "Export Journal & Progress",
        "settings_export_success" to "Data prepared for export",
        "settings_reset_data" to "Reset All Data & Restart Questionnaire",
        "settings_reset_confirm_title" to "Reset WordWalk?",
        "settings_reset_confirm_desc" to "This will erase all plans, completed sessions, and journal entries from this device. This cannot be undone.",
        "settings_privacy_note" to "100% Private & Device-Local: No account, no cloud servers, and no data tracking. Your journal remains strictly on your device.",

        // Notification strings
        "notif_channel_name" to "Daily Bible Walk Reminders",
        "notif_channel_desc" to "Gentle reminders for your daily WordWalk scripture time",
        "notif_title" to "Time for your WordWalk",
        "notif_body" to "A gentle 10-minute walk with Scripture awaits you today. Take a moment of calm."
    )

    private val sinhalaStrings = mapOf(
        // App identity
        "app_title" to "වර්ඩ්වෝක් (WordWalk)",
        "app_tagline" to "ශුද්ධ ලියවිල්ල සමඟ ඔබේ සන්සුන් දෛනික ගමන",
        "app_subtitle" to "ආධුනිකයන් සඳහා පුද්ගලීකරණය කළ බයිබල් අධ්‍යයන සහ ආධ්‍යාත්මික පුරුදු ඇප් එක",

        // General UI & Navigation
        "nav_home" to "අද දවස",
        "nav_study" to "අධ්‍යයනය",
        "nav_plan" to "සැලැස්ම",
        "nav_progress" to "ප්‍රගතිය",
        "nav_settings" to "සැකසුම්",
        "btn_continue" to "ඉදිරියට යන්න",
        "btn_back" to "ආපසු",
        "btn_next" to "ඊළඟ",
        "btn_save" to "සුරකින්න",
        "btn_complete" to "සැසිය සම්පූර්ණ කරන්න",
        "btn_completed" to "සම්පූර්ණයි",
        "btn_edit" to "සැලැස්ම වෙනස් කරන්න",
        "btn_pause" to "සැලැස්ම තාවකාලිකව නවත්වන්න",
        "btn_resume" to "නැවත අරඹන්න",
        "btn_close" to "වසන්න",
        "btn_cancel" to "අවලංගු කරන්න",
        "btn_apply" to "වෙනස්කම් යොදන්න",
        "btn_start_walk" to "අද දවසේ ගමන අරඹන්න",
        "btn_review" to "සැසිය සමාලෝචනය කරන්න",
        "btn_grace_catchup" to "කරුණාවේ විවේක දිනයක් ගන්න",

        // Language Switcher
        "lang_switch_title" to "භාෂාව / Language",
        "lang_english" to "English",
        "lang_sinhala" to "සිංහල",

        // Onboarding Questionnaire
        "onboarding_welcome_title" to "WordWalk වෙත සාදරයෙන් පිළිගනිමු",
        "onboarding_welcome_desc" to "කිසිදු පීඩනයකින් තොරව බයිබලය තේරුම් ගැනීමට, දිනපතා ස්ථාවර පුරුද්දක් ගොඩනගා ගැනීමට සහ සිතට සැනසීමක් ලබා ගැනීමට සුන්දර මඟක්.",
        "onboarding_step_of" to "පියවර %d / %d",
        "onboarding_resume_notice" to "නැවත සාදරයෙන් පිළිගනිමු! ඔබ පෙර නැවැත්වූ තැනින් ඉදිරියට යා හැක.",
        "onboarding_btn_start" to "ආරම්භ කරන්න",
        "onboarding_btn_resume" to "නැවතුණු තැනින් අරඹන්න",

        // Question 1: Language
        "q_lang_title" to "ඔබ කැමති භාෂාව තෝරන්න",
        "q_lang_desc" to "සැකසුම් මඟින් ඕනෑම වේලාවක මෙය වෙනස් කළ හැක.",

        // Question 2: Experience
        "q_exp_title" to "බයිබලය පිළිබඳ ඔබේ අත්දැකීම කෙබඳුද?",
        "q_exp_desc" to "ඔබේ මට්ටමට ගැලපෙන සරල පැහැදිලි කිරීම් ලබා දීමට මෙය උපකාරී වේ.",
        "exp_beginner" to "සම්පූර්ණ ආධුනිකයෙක්",
        "exp_beginner_desc" to "බයිබලයට නවකයෙක්, සංකීර්ණ වචන නැති සරල මඟපෙන්වීමක් සොයයි",
        "exp_explored" to "ස්වල්පයක් දනිමි",
        "exp_explored_desc" to "සමහර කතාන්දර දනිමි, නමුත් ක්‍රමානුකූල අවබෝධයක් අවශ්‍යයි",
        "exp_returning" to "නැවත ආරම්භ කරන්නෙක්",
        "exp_returning_desc" to "කාලයකට පසු නැවත සුහදව සම්බන්ධ වීමට සරල ආරම්භයක් සොයයි",
        "exp_consistent" to "ස්ථාවර පුරුද්දක් සොයන්නෙක්",
        "exp_consistent_desc" to "කියවා ඇත, නමුත් දිනපතා අඛණ්ඩව කරගෙන යාම අපහසුය",

        // Question 3: Goals
        "q_goals_title" to "ඔබේ ප්‍රධාන ආධ්‍යාත්මික අරමුණ කුමක්ද?",
        "q_goals_desc" to "මෙම ගමන තුළින් ඔබ බලාපොරොත්තු වන දෙය තෝරන්න.",
        "goal_habit" to "සන්සුන් දෛනික පුරුද්දක් ගොඩනැගීම",
        "goal_jesus" to "යේසුස් වහන්සේගේ ජීවිතය සහ ඉගැන්වීම් තේරුම් ගැනීම",
        "goal_peace" to "දෛනික සැනසීම, බලාපොරොත්තුව සහ ශක්තිය ලැබීම",
        "goal_apply" to "ජීවිතයට ප්‍රායෝගිකව යොදා ගැනීමට ඉගෙන ගැනීම",
        "goal_overcome" to "බයිබලය කියවීමට ඇති බිය හෝ පැකිලීම දුරු කර ගැනීම",

        // Question 4: Time per session
        "q_time_title" to "දිනකට කොපමණ කාලයක් වෙන් කළ හැකිද?",
        "q_time_desc" to "විනාඩි 5ක සන්සුන් අවධානය පවා ඔබේ දවස ආලෝකවත් කරයි. කාලයට වඩා අඛණ්ඩතාව වැදගත්ය.",
        "time_5min" to "විනාඩි 5 (කෙටි හා අවධානය යොමු කළ)",
        "time_10min" to "විනාඩි 10 (ආධුනිකයන් සඳහා නිර්දේශිතයි)",
        "time_15min" to "විනාඩි 15 (ගැඹුරු මෙනෙහි කිරීමක්)",
        "time_20min" to "විනාඩි 20 (සම්පූර්ණ අධ්‍යයනයක්)",

        // Question 5: Study Days
        "q_days_title" to "සතියකට දින කීයක් අධ්‍යයනය කිරීමට කැමතිද?",
        "q_days_desc" to "කිසිවිටක වරදකාරී හැඟීමක් ඇති නොවන පරිදි කරුණාවේ විවේක දින ඇතුළත් වේ.",
        "days_5" to "සතියට දින 5 (කරුණාවේ/විවේක දින 2ක් සහිතයි)",
        "days_6" to "සතියට දින 6 (කරුණාවේ දින 1ක් සහිතයි)",
        "days_7" to "සතියට දින 7 (දිනපතා ගමන)",

        // Question 6: Reminder time
        "q_reminder_title" to "දෛනික මතක් කිරීම අවශ්‍ය කුමන වේලාවටද?",
        "q_reminder_desc" to "මෘදු මතක් කිරීමක් ඔබේ නව පුරුද්ද ස්ථාවර කිරීමට උදව් වේ.",
        "rem_morning" to "උදෑසන (07:00 AM) — දවස සැනසිලිදායකව ඇරඹීමට",
        "rem_midday" to "දහවල් (12:30 PM) — කාර්යබහුල දවසේ කෙටි විරාමයක්",
        "rem_evening" to "සවස (08:30 PM) — දවස සන්සුන්ව නිමා කිරීමට",
        "rem_custom" to "වෙනත් වේලාවක්",

        // Question 7: Biggest challenge
        "q_challenge_title" to "ඔබ මුහුණ දුන් ලොකුම බාධාව කුමක්ද?",
        "q_challenge_desc" to "ඒ සඳහා විශේෂිත වූ මඟපෙන්වීම් සහ දිරිගැන්වීම් අපි ලබා දෙන්නෙමු.",
        "chal_time" to "කාර්යබහුල ජීවිතය නිසා කාලය මදිවීම",
        "chal_context" to "පුරාණ භාෂාව සහ පසුබිම තේරුම් ගැනීමට අපහසු වීම",
        "chal_consistency" to "දිනපතා දිගටම කරගෙන යාමේ අපහසුතාව",
        "chal_start" to "පටන් ගන්නේ කොතැනින්දැයි නොදැනීම",

        // Question 8: Plan Style
        "q_style_title" to "ඔබේ ආරම්භක සැලැස්ම තෝරන්න",
        "q_style_desc" to "ආධුනිකයන් සඳහා අපි ආදරයෙන් නිර්දේශ කරන්නේ යොහන් සුබාරංචියයි — යේසුස් වහන්සේගේ කතාවේ හදවත.",
        "style_john" to "යොහන් සුබාරංචිය: ජීවිතය සහ ඉගැන්වීම් (නිර්දේශිතයි)",
        "style_john_desc" to "යේසුස් වහන්සේ කවුදැයි උන්වහන්සේගේ හාස්කම් සහ ප්‍රේමය තුළින් දැනගන්න.",
        "style_wisdom" to "සරල ආරම්භය: ගීතාවලිය සහ ප්‍රඥාව",
        "style_wisdom_desc" to "සැනසීම, ප්‍රශංසාව සහ මඟපෙන්වීම පිළිබඳ කෙටි කාව්‍යමය මෙනෙහි කිරීම්.",
        "style_foundations" to "මූලික පදනම: ප්‍රධාන ඉගැන්වීම්",
        "style_foundations_desc" to "ඇදහිල්ල, ප්‍රේමය සහ සහජීවනය පිළිබඳ අත්‍යවශ්‍ය මූලධර්ම.",

        // Plan Generation Screen
        "plan_creating" to "ඔබේ පුද්ගලික WordWalk සැලැස්ම සකස් කරමින් පවතී...",
        "plan_ready_title" to "ඔබේ පුද්ගලීකරණය කළ සැලැස්ම සූදානම්!",
        "plan_title_john" to "යොහන් සුබාරංචිය: පියවරෙන් පියවර",
        "plan_summary" to "සැසි %d • සැසියකට විනාඩි %d • කරුණාවේ දින ඇතුළත්ය",
        "plan_start_date" to "ආරම්භක දිනය: %s",
        "plan_est_completion" to "නිමා වන ඇස්තමේන්තුගත දිනය: %s",
        "plan_grace_note" to "දයාවෙන් නිර්මාණය කර ඇත: මඟහැරුණු දින ලැජ්ජාවක් හෝ බිඳවැටුණු දඬුවමක් නැතිව ඉදිරියට ගෙන යයි.",
        "plan_sessions_label" to "සැලසුම් පාඩම් මාලාව",

        // Dashboard
        "dash_greeting_morning" to "සුභ උදෑසනක්",
        "dash_greeting_afternoon" to "සුභ දහවලක්",
        "dash_greeting_evening" to "සුභ සන්ධ්‍යාවක්",
        "dash_welcome_sub" to "සන්සුන් වන්න. අද ඔබේ ගමනට කරුණාව සහ ආශීර්වාදය ඇත.",
        "dash_today_card_title" to "අද දවසේ පාඩම",
        "dash_session_indicator" to "සැසි අංක %d / %d",
        "dash_est_time" to "විනාඩි %d ක ගමනක්",
        "dash_streak_label" to "දින අඛණ්ඩතාව",
        "dash_completed_sessions" to "සම්පූර්ණ කළ",
        "dash_plan_progress" to "සැලසුම් ප්‍රගතිය",
        "dash_missed_banner_title" to "ඊයේ මඟහැරුණාද? කරදර නොවන්න!",
        "dash_missed_banner_desc" to "ආධ්‍යාත්මික වර්ධනය යනු සන්සුන් ගමනකි, දැඩි ලැයිස්තුවක් නොවේ. විවේක දිනයක් ගන්න නැතහොත් දැන්ම ඉදිරියට යන්න.",
        "dash_verse_anchor_title" to "දෛනික සිතුවිලි නැංගුරම",

        // Daily Study Screen
        "study_tab_context" to "1. පසුබිම",
        "study_tab_reflection" to "2. මෙනෙහි කිරීම",
        "study_tab_notes" to "3. කරුණු",
        "study_tab_questions" to "4. ප්‍රශ්න",
        "study_tab_application" to "5. යෙදීම",
        "study_tab_journal" to "6. සටහන",
        "study_passage_heading" to "ලියවිලි යොමුව සහ ඓතිහාසික පසුබිම",
        "study_disclaimer" to "අධ්‍යයන සටහන: බයිබල් යොමු සහ මුල් ඉගෙනුම් සටහන්. මෙම මාර්ගෝපදේශය සමඟ ඔබේ ප්‍රියතම පරිවර්තනයද භාවිතා කරන්න.",
        "study_key_theme" to "ප්‍රධාන පණිවිඩය",
        "study_context_heading" to "ඓතිහාසික හා දෛනික පසුබිම",
        "study_reflection_heading" to "සිතා බැලීමට කරුණු",
        "study_learning_notes_heading" to "ආධුනිකයන් සඳහා ප්‍රධාන කරුණු",
        "study_questions_heading" to "සිතා බැලිය යුතු ප්‍රශ්න",
        "study_application_heading" to "අද දවසේ ප්‍රායෝගික පියවර",
        "study_journal_heading" to "යාච්ඤා සහ මෙනෙහි කිරීමේ සටහන",
        "study_journal_prompt_learned" to "අද මගේ හදවතට කතා කළේ කුමක්ද?",
        "study_journal_prompt_surrender" to "මා අතහැර දැමිය යුතු කරදරය කුමක්ද?",
        "study_journal_prompt_prayer" to "අද මගේ යාච්ඤාව හෝ ස්තූතිය...",
        "study_journal_placeholder" to "ඔබේ අදහස් නිදහසේ ලියන්න. සියල්ල ඔබගේ දුරකථනය තුළ සුරක්ෂිතව තැන්පත් වේ.",
        "study_complete_dialog_title" to "ඉතා අගෙයි!",
        "study_complete_dialog_desc" to "ඔබ අද දවසේ ලියවිල්ල සමඟ ගමන් කළා. මෙම වචන ඔබේ ඉදිරි කාලයට සැනසීම හා ආලෝකය ගෙනේවා.",
        "study_grace_day_title" to "කරුණාවේ සහ විවේක දිනය",
        "study_grace_day_desc" to "අද සැලසුම් කළ විවේක දිනයකි. පසුගිය සටහන් නැවත බලන්න, මඟහැරුණු දින සම්පූර්ණ කරන්න, නැතහොත් සන්සුන්ව විවේක ගන්න.",

        // Progress Screen
        "prog_title" to "ඔබේ ආධ්‍යාත්මික පුරුද්ද",
        "prog_stats_title" to "පුරුදු සාරාංශය",
        "prog_streak" to "දින %d ක් සක්‍රීයයි",
        "prog_total_completed" to "සැසි %d ක් සම්පූර්ණයි",
        "prog_minutes_spent" to "මුළු විනාඩි %d",
        "prog_milestones_title" to "සන්ධිස්ථාන සහ පදක්කම්",
        "prog_badge_first" to "පළමු පියවර",
        "prog_badge_first_desc" to "ඔබේ පළමු දෛනික ගමන සම්පූර්ණ කළා",
        "prog_badge_3day" to "තෙදින බීජය",
        "prog_badge_3day_desc" to "දින 3ක් අඛණ්ඩව ගමන් කළා",
        "prog_badge_7day" to "සත්දින පුරුද්ද",
        "prog_badge_7day_desc" to "සම්පූර්ණ කළ පාඩම් 7ක් සපුරා ගත්තා",
        "prog_badge_john_start" to "සුබාරංචි ගවේෂක",
        "prog_badge_john_start_desc" to "යේසුස් වහන්සේගේ ජීවන ගමන ආරම්භ කළා",
        "prog_badge_grace" to "කරුණාවේ ජයග්‍රාහකයා",
        "prog_badge_grace_desc" to "කිසිදු ලැජ්ජාවකින් තොරව කරුණාවේ දිනයක් භාවිතා කළා",
        "prog_past_journals_title" to "ඔබේ මෙනෙහි කිරීමේ සටහන්",
        "prog_empty_journal" to "ඔබ සම්පූර්ණ කළ සටහන් සහ යාච්ඤා මෙහි සුරැකෙනු ඇත.",

        // Settings Screen
        "settings_title" to "සැකසුම්",
        "settings_pref_section" to "මනාපයන්",
        "settings_lang_label" to "භාෂාව (Language)",
        "settings_notifications_section" to "දෛනික මතක් කිරීම්",
        "settings_reminder_toggle" to "දෛනික මතක් කිරීම සක්‍රීය කරන්න",
        "settings_reminder_time" to "මතක් කිරීමේ වේලාව",
        "settings_test_notification" to "පරීක්ෂණ දැනුම්දීමක් දැන් එවන්න",
        "settings_test_notification_sent" to "පරීක්ෂණ දැනුම්දීම යවන ලදී! ඔබගේ දැනුම්දීම් පරීක්ෂා කරන්න.",
        "settings_plan_section" to "ක්‍රියාකාරී සැලැස්ම",
        "settings_plan_status" to "සැලසුම් තත්ත්වය",
        "settings_plan_pace" to "සැසියක කාලය",
        "settings_backup_section" to "දත්ත සහ පෞද්ගලිකත්වය",
        "settings_export_data" to "සටහන් සහ ප්‍රගතිය පිටපත් කරන්න (Export)",
        "settings_export_success" to "දත්ත පිටපත් කිරීමට සූදානම් කරන ලදී",
        "settings_reset_data" to "සියලු දත්ත මකා නැවත ප්‍රශ්නාවලිය අරඹන්න",
        "settings_reset_confirm_title" to "WordWalk නැවත මුල සිට අරඹන්නද?",
        "settings_reset_confirm_desc" to "මෙමඟින් ඔබගේ සියලු සැලසුම්, සම්පූර්ණ කළ පාඩම් සහ සටහන් දුරකථනයෙන් මැකී යනු ඇත. මෙය නැවත ආපසු හැරවිය නොහැක.",
        "settings_privacy_note" to "100% පුද්ගලික සහ දුරකථනය තුළ පමණි: කිසිදු ගිණුමක් හෝ ක්ලවුඩ් සර්වරයක් නැත. ඔබගේ සටහන් ඔබගේ දුරකථනය තුළ පමණක් පවතී.",

        // Notification strings
        "notif_channel_name" to "දෛනික බයිබල් ගමන් මතක් කිරීම්",
        "notif_channel_desc" to "ඔබේ දෛනික WordWalk ලියවිලි කියවීමේ වේලාව සඳහා මෘදු මතක් කිරීම්",
        "notif_title" to "ඔබේ WordWalk වේලාව පැමිණ ඇත",
        "notif_body" to "ශුද්ධ ලියවිල්ල සමඟ සන්සුන් විනාඩි 10 ක ගමනක් අද ඔබ වෙනුවෙන් බලා සිටී. සිතට සැනසිලිදායක මොහොතක් ගන්න."
    )
}

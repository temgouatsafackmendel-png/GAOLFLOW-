package com.example.data.model

data class GoalTemplate(
    val title: String,
    val description: String,
    val category: GoalCategory,
    val targetType: GoalTargetType,
    val targetValue: Double,
    val unit: String,
    val durationMonths: Long = 3,
    val priority: GoalPriority = GoalPriority.HIGH,
    val suggestedMilestones: List<String> = emptyList(),
    val shortTermSacrifice: String = "",
    val longTermReward: String = ""
)

object GoalTemplatesRepository {
    val templates = listOf(
        GoalTemplate(
            title = "Constituer mon fonds de sécurité (500 000 FCFA)",
            description = "Avoir une sérénité financière absolue face aux imprévus, bâtir mon indépendance et protéger mes proches.",
            category = GoalCategory.FINANCES,
            targetType = GoalTargetType.NUMERIC,
            targetValue = 500000.0,
            unit = "FCFA",
            durationMonths = 5,
            priority = GoalPriority.HIGH,
            suggestedMilestones = listOf(
                "Ouvrir un compte d'épargne ou tontine dédiée",
                "Mettre de côté mes premiers 100 000 FCFA",
                "Franchir le cap de 250 000 FCFA (mi-parcours)",
                "Atteindre 400 000 FCFA",
                "Valider l'objectif total de 500 000 FCFA"
            ),
            shortTermSacrifice = "Renoncer aux achats impulsifs, aux dépenses d'apparence et aux sorties superflues.",
            longTermReward = "Sérénité absolue, liberté de choix, indépendance face aux dettes et respect de soi."
        ),
        GoalTemplate(
            title = "Lancer mon projet entrepreneurial",
            description = "Créer une source de revenus solide, développer mon leadership et concrétiser mon ambition économique.",
            category = GoalCategory.CARRIERE_ETUDES,
            targetType = GoalTargetType.CHECKLIST,
            targetValue = 5.0,
            unit = "étapes",
            durationMonths = 3,
            priority = GoalPriority.HIGH,
            suggestedMilestones = listOf(
                "Étudier le marché local et cibler le besoin client",
                "Établir le budget de départ et la rentabilité",
                "Créer l'identité visuelle et l'offre commerciale",
                "Réaliser mes 5 premières ventes ou clients pilotes",
                "Automatiser la gestion et réinvestir les bénéfices"
            ),
            shortTermSacrifice = "Sacrifier les soirées oisives et la peur du rejet pour travailler sans relâche.",
            longTermReward = "Liberté financière, fierté d'avoir bâti une entreprise et création d'emplois."
        ),
        GoalTemplate(
            title = "Lire 12 livres d'excellence et de business",
            description = "Élever mon niveau de compétence, enrichir ma pensée stratégique et développer l'état d'esprit des grands leaders.",
            category = GoalCategory.DEV_PERSO,
            targetType = GoalTargetType.NUMERIC,
            targetValue = 12.0,
            unit = "livres",
            durationMonths = 12,
            priority = GoalPriority.MEDIUM,
            suggestedMilestones = listOf(
                "Sélectionner 12 ouvrages de référence (management, finances, discipline)",
                "Lire au moins 15 pages chaque matin sans distraction",
                "Noter les 3 meilleures leçons de chaque livre dans un carnet"
            ),
            shortTermSacrifice = "Remplacer 45 min de divertissement passif sur les réseaux sociaux par la concentration.",
            longTermReward = "Une clarté d'esprit redoutable, un vocabulaire riche et une avance stratégique sur 99% des gens."
        ),
        GoalTemplate(
            title = "Discipline physique : 30 min de sport par jour",
            description = "Forger une santé d'acier, développer mon énergie vitale et dominer mon corps par l'auto-discipline.",
            category = GoalCategory.SANTE_SPORT,
            targetType = GoalTargetType.HABIT,
            targetValue = 30.0,
            unit = "jours",
            durationMonths = 1,
            priority = GoalPriority.HIGH,
            suggestedMilestones = listOf(
                "Préparer ma tenue de sport la veille au soir",
                "Séance matinale : 50 pompes + étirements + gainage",
                "Valider 7 jours d'affilée sans abandonner"
            ),
            shortTermSacrifice = "L'inconfort du réveil matinal, la sueur et la paresse du lit douillet.",
            longTermReward = "Une énergie inaltérable, un corps sculpté, une santé robuste et une confiance indestructible."
        ),
        GoalTemplate(
            title = "Maîtriser une compétence monétisable",
            description = "Apprendre une compétence à forte valeur ajoutée pour augmenter mes revenus et ma valeur sur le marché.",
            category = GoalCategory.CARRIERE_ETUDES,
            targetType = GoalTargetType.NUMERIC,
            targetValue = 60.0,
            unit = "heures",
            durationMonths = 2,
            priority = GoalPriority.HIGH,
            suggestedMilestones = listOf(
                "Choisir la compétence clé (développement, marketing, finance, design)",
                "Suivre 20 heures de formation et pratique concrète",
                "Réaliser un projet réel ou portfolio",
                "Trouver mon premier contrat ou mission rémunérée"
            ),
            shortTermSacrifice = "Les doutes, la frustration d'apprendre quelque chose de difficile au début.",
            longTermReward = "La capacité de générer des revenus n'importe où et l'autonomie professionnelle."
        ),
        GoalTemplate(
            title = "Épargne d'investissement mensuelle (100 000 FCFA)",
            description = "Construire un patrimoine durable par une rigueur mensuelle non négociable.",
            category = GoalCategory.FINANCES,
            targetType = GoalTargetType.NUMERIC,
            targetValue = 100000.0,
            unit = "FCFA",
            durationMonths = 1,
            priority = GoalPriority.HIGH,
            suggestedMilestones = listOf(
                "Isoler les 100 000 FCFA dès la réception de mes revenus",
                "Éviter toute dépense impulsive ce mois-ci"
            ),
            shortTermSacrifice = "Différer le plaisir de consommer immédiatement l'argent gagné.",
            longTermReward = "Un capital d'investissement qui travaille pour vous et crée la richesse générationnelle."
        )
    )
}

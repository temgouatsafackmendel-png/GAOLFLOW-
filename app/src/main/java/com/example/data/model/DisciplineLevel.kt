package com.example.data.model

data class DisciplineLevel(
    val levelNumber: Int,
    val name: String,
    val title: String,
    val minXp: Int,
    val maxXp: Int,
    val philosophy: String,
    val unlockCriteria: String,
    val actionChallenge: String,
    val colorHex: Long
)

object DisciplineLevelSystem {
    val levels = listOf(
        DisciplineLevel(
            levelNumber = 1,
            name = "Niveau 1",
            title = "L'Éveil & L'Ancrage",
            minXp = 0,
            maxXp = 120,
            philosophy = "La discipline commence par la clarté. Vous arrêtez de subir vos journées pour choisir consciemment vos priorités.",
            unlockCriteria = "Créer ses 2 premiers objectifs et définir avec précision son « Pourquoi ».",
            actionChallenge = "Exécuter chaque matin 1 action sans la reporter au lendemain.",
            colorHex = 0xFF3B82F6
        ),
        DisciplineLevel(
            levelNumber = 2,
            name = "Niveau 2",
            title = "La Rigueur Quotidienne",
            minXp = 120,
            maxXp = 350,
            philosophy = "L'excitation du début retombe, c'est ici que naît la véritable discipline : agir même quand l'envie faiblit.",
            unlockCriteria = "Atteindre 120 XP et maintenir une série active de 3 jours consécutifs.",
            actionChallenge = "Refuser toute négociation intérieure avec la paresse.",
            colorHex = 0xFF10B981
        ),
        DisciplineLevel(
            levelNumber = 3,
            name = "Niveau 3",
            title = "L'Élan & Le Momentum",
            minXp = 350,
            maxXp = 800,
            philosophy = "L'effort devient régulier. Les jalons tombent les uns après les autres avec méthode et efficacité.",
            unlockCriteria = "Atteindre 350 XP et accomplir son 1er grand objectif à 100%.",
            actionChallenge = "Anticiper les imprévus et maintenir la cadence sans faillir.",
            colorHex = 0xFFF59E0B
        ),
        DisciplineLevel(
            levelNumber = 4,
            name = "Niveau 4",
            title = "L'Auto-Discipline Intérieure",
            minXp = 800,
            maxXp = 1600,
            philosophy = "Vous n'avez plus besoin d'effort surhumain : l'action est devenue votre état naturel. Vous tenez parole envers vous-même.",
            unlockCriteria = "Atteindre 800 XP et afficher au moins 14 jours de série d'habitudes.",
            actionChallenge = "Protéger farouchement votre temps et éliminer les distractions inutiles.",
            colorHex = 0xFF8B5CF6
        ),
        DisciplineLevel(
            levelNumber = 5,
            name = "Niveau 5",
            title = "La Maîtrise & Le Leadership",
            minXp = 1600,
            maxXp = 3000,
            philosophy = "La discipline est gravée en vous. Vos ambitions se transforment systématiquement en réalisations concrètes.",
            unlockCriteria = "Atteindre 1600 XP et avoir validé plus de 3 objectifs d'envergure.",
            actionChallenge = "Viser des projets d'impact national ou international et élever vos pairs.",
            colorHex = 0xFF0D9488
        )
    )

    fun getLevelForXp(xp: Int): DisciplineLevel {
        return levels.lastOrNull { xp >= it.minXp } ?: levels.first()
    }
}

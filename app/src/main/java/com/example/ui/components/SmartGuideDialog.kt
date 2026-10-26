package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.PrimaryIndigo

data class SmartPillar(
    val letter: String,
    val title: String,
    val explanation: String,
    val example: String,
    val color: Color
)

@Composable
fun SmartGuideDialog(onDismiss: () -> Unit) {
    val pillars = listOf(
        SmartPillar(
            letter = "S",
            title = "Spécifique",
            explanation = "Définissez clairement ce que vous voulez accomplir sans ambiguïté.",
            example = "Au lieu de « faire du sport », dites « courir 3 fois par semaine ».",
            color = Color(0xFF3B82F6)
        ),
        SmartPillar(
            letter = "M",
            title = "Mesurable",
            explanation = "Associez un indicateur chiffré ou des jalons pour suivre votre progression.",
            example = "« Épargner 2 000 € » ou « Lire 12 livres ».",
            color = Color(0xFF10B981)
        ),
        SmartPillar(
            letter = "A",
            title = "Atteignable",
            explanation = "Placez la barre suffisamment haut pour être motivé, mais assez réaliste pour ne pas abandonner.",
            example = "Découpez un grand projet en petits jalons progressifs.",
            color = Color(0xFFF59E0B)
        ),
        SmartPillar(
            letter = "R",
            title = "Réaliste & Pertinent",
            explanation = "Votre objectif doit avoir du sens pour VOUS (votre « Pourquoi »).",
            example = "Quel impact positif aura-t-il sur votre vie ?",
            color = Color(0xFF8B5CF6)
        ),
        SmartPillar(
            letter = "T",
            title = "Temporellement défini",
            explanation = "Fixez une date butoir précise. Une échéance crée l'urgence saine et évite la procrastination.",
            example = "« Atteindre cet objectif avant le 31 décembre ».",
            color = Color(0xFFEC4899)
        )
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(PrimaryIndigo.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = "Conseils",
                            tint = PrimaryIndigo,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Méthode S.M.A.R.T.",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Comment réussir à coup sûr",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Fermer")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Pour maximiser vos chances de concrétiser vos ambitions, structurez chacun de vos objectifs selon ces 5 piliers éprouvés :",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                pillars.forEach { pillar ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                    ) {
                        Row(modifier = Modifier.padding(12.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(pillar.color),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = pillar.letter,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = pillar.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = pillar.explanation,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Exemple : ${pillar.example}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Medium,
                                    color = pillar.color
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("J'ai compris, passer à l'action !")
                }
            }
        }
    }
}

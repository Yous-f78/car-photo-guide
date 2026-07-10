package com.example.carguide

/**
 * Defines the three car templates.
 *
 * Each template has:
 *  - [widthRatio] : fraction of the view width the guide rectangle occupies.
 *  - [heightRatio]: fraction of the view height the guide rectangle occupies.
 *  - [label]      : human-readable name.
 *
 * Aspect ratio is kept consistent (4:3) across all templates so that photos
 * look comparable; only the overall scale changes.
 *
 * Citadine  -> smallest  (65% width)
 * Berline  -> medium    (78% width)
 * SUV      -> largest   (90% width)
 */
enum class CarType(
    val label: String,
    val widthRatio: Float,
    val heightRatio: Float
) {
    CITADINE("Citadine",   0.65f, 0.39f),   // 4:3  -> 0.65 * 3/4 = 0.4875 -> ~0.49
    BERLINE("Berline",     0.78f, 0.46f),
    SUV("Monospace / SUV", 0.90f, 0.54f);
}

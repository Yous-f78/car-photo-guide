package com.example.carguide

/**
 * Defines the three car templates.
 *
 * Each template has:
 *  - [widthRatio] : fraction of the view width the guide rectangle occupies.
 *  - [heightRatio]: fraction of the view height the guide rectangle occupies.
 *  - [label]      : human-readable name.
 *
 * The overlay is designed for LANDSCAPE 4:3 photos (wider than tall).
 * The widthRatio controls how much of the screen width the frame spans;
 * heightRatio is derived to keep a 4:3-ish inner frame that fits the car.
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
    CITADINE("Citadine",   0.65f, 0.55f),
    BERLINE("Berline",     0.78f, 0.66f),
    SUV("Monospace / SUV", 0.90f, 0.76f);
}

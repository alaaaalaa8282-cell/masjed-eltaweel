# Design System Document: The Sacred Interval

## 1. Overview & Creative North Star
**Creative North Star: "The Ethereal Sanctuary"**

This design system rejects the "utility-first" clutter of traditional prayer apps. Instead, it adopts a high-end editorial approach—treating time and prayer as a curated, sacred experience. We move beyond the "app as a tool" and toward "app as a space."

The experience is defined by **Atmospheric Breathing Room**. We break the standard rigid grid by employing intentional asymmetry: prayer times are not just rows in a table; they are poetic markers on a journey. By utilizing deep tonal depth and overlapping editorial typography, we create a digital environment that feels as serene as a quiet prayer hall at dawn.

---

### 2. Colors: Tonal Depth over Borders
The palette is a sophisticated interplay of `primary` (Forest Shadow) and `secondary` (Ochre Gold), grounded by a spectrum of "Linen" neutrals.

*   **The "No-Line" Rule:** Visual separation is achieved exclusively through background shifts. Never use a 1px solid border to section content. To separate the "Current Prayer" from the "Upcoming List," transition from `surface` to `surface-container-low`.
*   **Surface Hierarchy & Nesting:** Use a "Stacked Vellum" approach. 
    *   **Level 0 (Base):** `surface` (#f8f9fa) for the main viewport.
    *   **Level 1 (Section):** `surface-container-low` (#f3f4f5) for large content blocks.
    *   **Level 2 (Interaction):** `surface-container-lowest` (#ffffff) for the prayer cards themselves, creating a subtle "lift" against the off-white background.
*   **The "Glass & Gradient" Rule:** For the main Adhan countdown or Hero section, use a gradient transition from `primary` (#012d1d) to `primary_container` (#1b4332). Floating action elements should use Glassmorphism: `surface_variant` at 60% opacity with a `24px` backdrop blur.
*   **Signature Textures:** Apply a subtle grain overlay or a very soft linear gradient (Primary to Primary Fixed Dim) on large action buttons to provide a "silk-touch" tactile quality.

---

### 3. Typography: Editorial Grace
We pair the intellectual rigor of **Noto Serif** with the modern clarity of **Manrope**.

*   **Display (Noto Serif):** Used for the "Current Time" or "Prayer Name." It should feel authoritative yet poetic. Use `display-lg` for the primary countdown to create a focal point.
*   **Headline (Noto Serif):** Reserved for location headers and section titles (e.g., "Makkah, Saudi Arabia"). These should feel like titles in a premium travel journal.
*   **Body & Labels (Manrope):** All functional data—times, settings, and descriptions—use Manrope. The high x-height ensures legibility even when rendered in the more muted `on_surface_variant`.
*   **Hierarchy Tip:** Contrast `display-sm` (Serif) with `label-md` (Sans, All-Caps, Letter-spaced +0.05rem) to create a sophisticated, high-fashion editorial hierarchy.

---

### 4. Elevation & Depth: The Tonal Layer
In this system, shadows are light, and structure is felt rather than seen.

*   **The Layering Principle:** Avoid shadows on static cards. Instead, place a `surface-container-lowest` card on a `surface-container-low` background. The slight shift in "white" creates a naturalistic depth.
*   **Ambient Shadows:** For floating elements (like a location picker), use an extra-diffused shadow: `offset: 0 12px`, `blur: 32px`, `color: primary (at 6% opacity)`. This creates a glow rather than a harsh drop-shadow.
*   **The "Ghost Border" Fallback:** If accessibility requires a container boundary, use `outline_variant` at **15% opacity**. It should be a whisper of a line.
*   **Glassmorphism:** Use semi-transparent `primary_container` for persistent bottom bars to allow the "spiritual green" to bleed through the navigation.

---

### 5. Components

#### Prayer Cards (The Hero)
*   **Structure:** No dividers. Use `xl` (1.5rem) rounded corners.
*   **State:** The "Active Prayer" card uses the `primary` background with `on_primary` text. Upcoming prayers use `surface-container-lowest` with `on_surface` text.
*   **Detail:** Include a soft `secondary_fixed` (Gold) accent line (2px thick, vertical) only on the active prayer card to denote "Now."

#### Switches & Toggles (Settings)
*   **Track:** Use `surface_container_highest` for the "off" state.
*   **Thumb:** The toggle thumb should always be `secondary` (Gold) when active, providing a warm, premium tactile feel.
*   **Shape:** `full` (9999px) roundedness for a pill-shaped, organic feel.

#### Location Headers
*   **Layout:** Left-aligned `headline-md` (Noto Serif). 
*   **Subtext:** Below the city, place the coordinates or date in `label-sm` (Manrope) using `secondary` (Gold) to add a touch of "jewelry" to the header.

#### Inputs & Settings Rows
*   **Style:** Forbid the use of input boxes. Use "Underlined" styles with the `outline_variant` at 20% opacity.
*   **Interaction:** On focus, the underline transitions to `secondary` (Gold).

---

### 6. Do’s and Don'ts

*   **DO:** Use generous white space. If you think there is enough space, add 16px more.
*   **DO:** Use `secondary` (Gold) sparingly. It is a "divine accent," not a primary background.
*   **DO:** Overlap elements. A prayer card can slightly overlap a header image to break the "blocky" feel.
*   **DON'T:** Use 100% black (#000000). Always use `on_surface` (#191c1d) or `primary` (#012d1d) for text to maintain softness.
*   **DON'T:** Use standard "Material Design" shadows. They are too industrial for this spiritual context.
*   **DON'T:** Use sharp corners. Stick to the `xl` and `lg` roundedness scale to keep the interface "soft" to the touch.
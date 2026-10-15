# Implementation Plan - Studio LuX Refinement & Engine Alignment

## Overview
This plan covers the comprehensive integration and verification of all requested features for Studio LuX:
1. Streamlined chat header with dropdown menu and the unified multi-AI control button (Gemini Cloud, Gemma Vertex, Gemma3 270m Local via Termux / localhost 90.11.29.1:4250, and Antigravity CLI Hybrid).
2. Quick shortcuts for Terminal, Permissions (Shield), LuX Live Chatbot, and Floating HUD with adjustable size and transparency.
3. First-run startup sequence with automated initialization, permission synchronization, custom sound notification chime, and background kernel persistence.
4. Gold & Silver metallic styling with luxury translucent glass borders and transparent high-contrast text.

## Proposed Changes

### 1. UI & Navigation Polish (`AiOrchestratorScreen.kt` & MainActivity)
- Verify and ensure the top bar of the main chat interface contains strictly the dropdown menu and the unified multi-AI button container.
- Ensure shortcut buttons (Terminal, Permissions, LuX Live, Settings) are present and properly routed.

### 2. AI Engines & Termux Local Integration (`AiOrchestratorService.kt` & `GemmaModelManager.kt`)
- Verify Gemma3 270m local server binding to `https://90.11.29.1:4250` via Termux initialization script.
- Validate hybrid routing for Antigravity CLI with Google Cloud / Drive sync.

### 3. Startup & Background Service (`LuxPriorityKernelService.kt` & `LuxAnimatedIntroScreen.kt`)
- Ensure first-run startup sequence triggers full permission checks, sound chime synthesis, and background execution.

## Verification Plan
- Run `compile_applet` to verify clean build.
- Run Robolectric unit tests via `gradle :app:testDebugUnitTest`.

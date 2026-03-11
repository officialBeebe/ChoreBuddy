# Chore Buddy

Chore Buddy is a household chore tracking application exploring two contrasting architectures:

- A cloud-backed web MVP (Vite + React + DynamoDB)
- A privacy-first, offline-capable Android MVP

Users can set timers, track due dates, configure optional repetition, and export completion history as PDF reports.

## Project Status

A fully functioning Android MVP is provided with the core feature set. Since the project was initially conceived as an AWS-backed web app, the legacy Vite build is also provided inside `chore-app-ui/`.

## Project Scope

This repository supports a B.S. Software Engineering capstone project.

### 1. Web MVP

- Validate design and user workflow
- (Bonus) Serve as an AWS reference architecture

### 2. Android Application

- Demonstrate a fully functional Android MVP
- Offline only; no external services or telemetry
- Local persistence using Room / SQLite

Both implementations intentionally explore architectural—if not philosophical—tradeoffs within the same problem domain. Personal assistants can do their jobs just fine without imposing privacy and security tradeoffs on the user.

## Web MVP Architecture

The web MVP demonstrates a cloud architecture aligned with AWS well-architected concepts:

- React SPA
- AWS Amplify
- API Gateway
- AWS Lambda
- DynamoDB (single-table, household-scoped data model)

Backend functionality is intentionally scoped for UI exploration and architectural clarity rather than full production hardening.

## Android Application

The Android application deliberately diverges from the web MVP and is designed as an offline, privacy-first app.

### Characteristics

- Java with core Android SDK
- Local persistence using Room / SQLite
- Offline; no external services or telemetry
- Gesture-based navigation with Floating Action Buttons (FABs)
- Material Design layouts with minimal visual and cognitive overhead

### Implemented Functionality

- Household chore timers with completion tracking
- Local persistence of timer and history data
- ~~User-configured alerts and reminders~~
- Export of completion history as table-formatted reports via Android system intents

This approach prioritizes data ownership, transparency, and user control without external dependencies.

## Build References (Web MVP)

### REST API → Lambda → DynamoDB

https://docs.aws.amazon.com/apigateway/latest/developerguide/http-api-dynamo-db.html

> A valid DynamoDB table URL is required in `.env*`:
> `VITE_API_BASE_URL=https://<your-private-string-here>.execute-api.us-east-2.amazonaws.com`

### React UI

Created using Vite with a React + TypeScript template.

### Deployment

React UI deployed via AWS Amplify.

> Self-hosted deployments are also supported — the only requirement is a valid DynamoDB URL; see above.

## Network Diagram (Web MVP)

Retained for reference.

<img width="762" height="305" alt="ChoresTimerAWSReactApp" src="https://github.com/user-attachments/assets/fb69c597-97d3-4431-bfe0-a498d0c58113" />
# Chore Buddy

Chore Buddy is an Android application for tracking household chores, including due dates, optional
repetition, and completion history. Users can set timers and generate PDF reports of past progress. Data
is stored locally, the app does not require network connectivity, and it requests only the permissions
needed to function.

Household chore timer application exploring two contrasting architectures:

- a cloud-backed web MVP
- a privacy-first, offline-capable Android MVP

## Project Status

Android MVP code will be added once the core feature set stabilizes. A fully functioning Android MVP is provided with the core feature set.

Additionally, since the project was initially conceived of a AWS-backed web app, the legacy Vite build is provided along inside of `chore-app-ui/`.

## Project Scope

This repository supports a B.S. Software Engineering capstone project.

### 1. Web MVP (current contents)

- Validate design and user workflow
- (Bonus) Serve as an AWS reference architecture

### 2. Android Application

- Demonstrate fully functional Android MVP
- Offline only; no external services or telemetry
- Secure data access utilizing Room Sqlite3 API

Both implementations intentionally explore architectural--if not philisophical--tradeoffs within the same problem domain. Personal assistants can do their jobs just fine without imposing privacy and security tradeoffs.

## Web MVP Architecture

The existing web MVP demonstrates a cloud architecture aligned with AWS well-architected concepts:

- React SPA
- AWS Amplify
- API Gateway
- AWS Lambda
- DynamoDB (single-table, household-scoped data model)

Backend functionality is intentionally scoped for UI exploration and architectural clarity rather than full production hardening.

## Android Application Direction

The Android application deliberately diverges from the web MVP and is designed as an offline, privacy-first Android app.

### Current characteristics

- Java with core Android SDK
- Local persistence using Room
- Offline; no external services
- Gesture-based navigation with Floating Action Buttons (FABs)
- Material Design layouts with minimal visual and cognitive overhead

### Implemented functionality

- Household chore timers with completion tracking
- Local persistence of timer and history data
- ~~User-configured alerts and reminders~~
- Export of completion history as table-formatted reports to Android system implicits.

This approach prioritizes data ownership, transparency, and user control without external dependencies.

## Build References (Web MVP)

### REST API → Lambda → DynamoDB

https://docs.aws.amazon.com/apigateway/latest/developerguide/http-api-dynamo-db.html

> You must include a proper DynamoDB table URL in `.env*`. I.e. `VITE_API_BASE_URL=https://<your-private-string-here>.execute-api.us-east-2.amazonaws.com`

### React UI

Created using Vite with a React + TypeScript template.

### Deployment

React UI deployed via AWS Amplify.

> Of course self-hosted is an option. The only requirement in the Vite MVP is a valid DynamoDB table... see above.

## Network Diagram (Web MVP)

Retained for reference.

<img width="762" height="305" alt="ChoresTimerAWSReactApp" src="https://github.com/user-attachments/assets/fb69c597-97d3-4431-bfe0-a498d0c58113" />

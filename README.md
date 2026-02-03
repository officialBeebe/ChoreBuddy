# Chore Buddy

Household chore timer application exploring two contrasting architectures:
- a cloud-backed web MVP
- a privacy-first, offline-capable Android application

---

## Project Status

This repository currently contains a **web-based MVP** developed earlier in the project lifecycle.  
The primary application is now being rebuilt as a **privacy-focused, offline-first Android app**.

Android MVP code will be added once the core feature set stabilizes.

---

## Project Scope

This repository supports a B.S. Software Engineering capstone project and serves two purposes:

### 1. Web MVP (current contents)
- Validate user flows and interaction design
- Demonstrate system boundaries and service responsibilities
- Serve as an AWS Solutions Architect Associate–aligned reference architecture

### 2. Android Application (in progress)
- Local-first, offline-capable design
- Privacy-oriented defaults (no background services, alerts off by default)
- Minimal data collection with explicit user control

Both implementations intentionally explore architectural tradeoffs within the same problem domain.

---

## Web MVP Architecture

The existing web MVP demonstrates a managed-services architecture aligned with AWS SAA concepts:

- React SPA
- AWS Amplify
- API Gateway
- AWS Lambda
- DynamoDB (single-table, household-scoped data model)

Backend functionality is intentionally scoped for UI exploration and architectural clarity rather than full production hardening.

---

## Android Application Direction

The Android application deliberately diverges from the web MVP and is designed as a privacy-first, offline-capable mobile app.

### Current characteristics
- Java with core Android SDK
- Local persistence using Room
- Offline operation by default; no required cloud services
- Gesture-based navigation with a single Floating Action Button (FAB)
- Material Design layouts with minimal visual and cognitive overhead

### Implemented functionality
- Household chore timers with completion tracking
- Local persistence of timer and history data
- User-configured alerts and reminders

### Planned additions
- Export of completion history as CSV and PDF-style reports
- Final save and reporting workflows for the MVP

This approach prioritizes data ownership, transparency, and user control without external dependencies.

---

## Build References (Web MVP)

### REST API → Lambda → DynamoDB
https://docs.aws.amazon.com/apigateway/latest/developerguide/http-api-dynamo-db.html

### React UI
Created using Vite with a React + TypeScript template.

### Deployment
React UI deployed via AWS Amplify.

---

## Network Diagram (Web MVP)

Retained for reference.


<img width="762" height="305" alt="ChoresTimerAWSReactApp" src="https://github.com/user-attachments/assets/fb69c597-97d3-4431-bfe0-a498d0c58113" />

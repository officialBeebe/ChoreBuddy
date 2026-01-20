# chore-app

Household chore timers app that doubles as an AWS SAA-aligned reference architecture.

Use managed AWS services with clear boundaries:

- React SPA
- Amplify
- API Gateway
- Lambda
- DynamoDB (single-table, household-scoped data)

## Project Intent & Scope

This repository contains a UI-focused MVP developed to support an Android application built as part of a B.S. Software Engineering capstone project.

The primary goals of this MVP are:
- Validate user flows and interaction design
- Demonstrate system boundaries and service responsibilities
- Serve as a reference architecture aligned with AWS SAA concepts

While the architecture reflects production patterns, backend functionality is intentionally
scoped to support UI demonstration and iteration rather than full production hardening. 

## Architectural Context

This web-based MVP explores a cloud-backed, managed-services architecture aligned with AWS SAA concepts. The companion Android capstone application intentionally adopts a different approach—local-first, offline-capable, and data-sovereign by design. The two implementations represent a deliberate comparison of architectural tradeoffs within the same problem domain.

## Build: REST API -> Lamda -> DynamoDB

https://docs.aws.amazon.com/apigateway/latest/developerguide/http-api-dynamo-db.html

> [Clean up](https://docs.aws.amazon.com/apigateway/latest/developerguide/http-api-dynamo-db.html#http-api-dynamo-db-cleanup)

## Build: React UI

`npm create vite@latest chore-app-ui -- --template react-ts`

## Deploy: React UI to Amplify

https://docs.aws.amazon.com/amplify/latest/userguide/getting-started-next.html

## Network Diagram

<img width="762" height="305" alt="ChoresTimerAWSReactApp" src="https://github.com/user-attachments/assets/fb69c597-97d3-4431-bfe0-a498d0c58113" />

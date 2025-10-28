# Shodh-a-Code

A full-stack online coding contest platform that enables participants to join live contests, solve programming problems in multiple languages, and view real-time leaderboards.

---

## Table of Contents

- [Overview](#overview)
- [Demo](#demo)
- [Usage](#usage)
- [How It Works](#how-it-works)
  - [Architecture](#architecture)
  - [Core Flow](#core-flow)
  - [Detailed Flow with API Endpoints](#detailed-flow-with-api-endpoints)
- [Features](#features)
- [Setup](#setup)
  - [Docker Compose Setup](#docker-compose-setup)
  - [Manual Setup](#manual-setup)
- [API Reference](#api-reference)
  - [Contest Endpoints](#contest-endpoints)
  - [Submission Endpoints](#submission-endpoints)
- [Design Choices](#design-choices)
  - [Backend Design](#backend-design)
  - [Frontend Design](#frontend-design)
  - [Docker Orchestration](#docker-orchestration)
  - [Scoring and Leaderboard](#scoring-and-leaderboard)
- [Key Challenges](#key-challenges)
- [Project Structure](#project-structure)
- [Key Things to Remember](#key-things-to-remember)
- [Authors](#authors)

---

## Overview

Shodh-a-Code replicates the experience of competitive coding platforms like **Codeforces** or **LeetCode** in a self-contained system. It consists of a **Spring Boot backend** for problem management, code evaluation, and scoring, and a **React frontend (NextJS)** for the contest UI, editor, and leaderboard.

---

## Demo

[Youtube Video Link](https://www.youtube.com/watch?v=2Nu6t862cdA)


---

## Usage

1. Start backend and frontend
2. Join contest by entering the contestId and a username (creates a participant)
3. Select a problem and write code in the editor
4. Submit - backend compiles/runs inside Docker
5. Verdict appears in output panel
6. On `ACCEPTED`, `/update-score` updates points
7. Leaderboard updates automatically

**Note**: Contest ID is hardcoded as 1 for this assignment

---

## How It Works

### Architecture

#### Frontend (Next.js + React)

Provides an interactive contest dashboard with problem navigation, live verdict updates, and a code editor.

#### Backend (Spring Boot)

- Manages contests, problems, users, and submissions
- Executes user code inside Docker containers for language isolation
- Captures outputs for test-case validation

#### Judge Engine (Docker-based)

- Each submission runs inside a sandboxed Docker container with CPU and memory limits
- The backend compares the program output to expected test-case results
- Assigns verdicts such as `ACCEPTED`, `WRONG_ANSWER`, `RUNNING` or `TLE`

### Core Flow

1. A user joins a contest using a username and contest ID and gets redirected to the main contest page
2. The backend creates or retrieves their participant record
3. The user submits a solution via the editor and the status is continuously updated and displayed via polling
4. The code is compiled and run in Docker, and outputs are validated
5. On an `ACCEPTED` verdict, the frontend triggers a backend score update call
6. The backend checks if the problem was already solved by that user. If not, it increments the score and marks it as solved
7. The leaderboard updates automatically via polling every 15 seconds

### Detailed Flow with API Endpoints

1. A user joins a contest using a username and contest ID (`POST /api/contests/{contestId}/join`) and gets redirected to the main contest page (`GET /api/contests/{contestId}`)
2. The backend creates or retrieves their participant record and adds them to the leaderboard
3. The user submits a solution via the editor (`POST /api/submissions`) and the status is continuously updated and displayed via polling (`GET /api/submissions/{id}`)
4. The code is compiled and run in Docker, and outputs are validated against test cases
5. On an `ACCEPTED` verdict, the frontend triggers a backend score update call (`POST /api/contests/{contestId}/update-score`)
6. The backend checks if the problem was already solved by that user. If not, it increments the score and marks it as solved
7. The leaderboard updates automatically via polling (`GET /api/contests/{contestId}/leaderboard`) every 15 seconds

---

## Features

- **Multi-language support**: Java, C++, and Python
- **Real-time verdict updates and scoring**
- **Docker-based secure code execution**
- **Persistent solved-problem tracking** (no duplicate scoring)
- **Lightweight database** (H2 or SQL)
- **Live leaderboard** for each contest
- **Responsive React frontend** with syntax-highlighted editor
- **Persistent Code Editor** Preserved code across sessions and page reloads.

---

## Setup

### Docker Compose Setup

For a streamlined setup using Docker Compose:

```bash
git clone https://github.com/AbhinavOC24/shodh-a-code.git
cd shodh-a-code
docker-compose up --build
```

**Note:** This takes some time because the container needs to pull compiler images of respective languages.

This launches both backend and frontend together.

**Default backend runs at:** `http://localhost:8080`  
**Default frontend runs at:** `http://localhost:3000`

### Manual Setup

**Prerequisites:**

- Java 17+
- Node.js 18+
- Docker
- npm or pnpm
- Minimum 8 GB RAM (for Docker containers)

**1. Clone the Repository**

```bash
git clone https://github.com/AbhinavOC24/shodh-a-code.git
cd shodh-a-code
```

**2. Backend Setup**

```bash
cd backend
./mvnw clean install
./mvnw spring-boot:run
```

**Default backend runs at:** `http://localhost:8080`

**3. Frontend Setup**

```bash
cd frontend
npm install
npm run dev
```

**Default frontend runs at:** `http://localhost:3000`

**4. Verify Docker Setup**

```bash
docker --version
docker ps
```

Ensure the following base images are available:

- `gcc:13.2.0`
- `azul/zulu-openjdk:17`
- `python:3.11-alpine`

---

## API Reference

### Contest Endpoints

| Endpoint                          | Method | Description                               |
| --------------------------------- | ------ | ----------------------------------------- |
| `/api/contests/{id}`              | GET    | Get contest details and problems          |
| `/api/contests/{id}/join`         | POST   | Join a contest with username              |
| `/api/contests/{id}/leaderboard`  | GET    | Fetch current leaderboard                 |
| `/api/contests/{id}/update-score` | POST   | Update user score after solving a problem |

#### 1. Get Contest Details

**Endpoint:** `GET /api/contests/{id}`

**Description:** Retrieves contest details including all problems and test cases

**Response Example:**

```json
{
  "id": 1,
  "title": "Shodh-a-Code Demo Contest",
  "code": "DEMO2025",
  "problems": [
    {
      "id": 1,
      "title": "A + B Problem",
      "code": "ADD001",
      "statement": "Read two integers and output their sum.",
      "language": "java",
      "score": 100,
      "testCases": [
        {
          "id": 1,
          "inputData": "2 3",
          "expectedOutput": "5"
        },
        {
          "id": 2,
          "inputData": "10 5",
          "expectedOutput": "15"
        }
      ]
    }
  ]
}
```

#### 2. Join Contest

**Endpoint:** `POST /api/contests/{id}/join`

**Description:** Registers a user for the contest and creates their participant record

**Request Body:**

```json
{
  "username": "Abhi1"
}
```

**Response Example:**

```json
{
  "joined": true,
  "user": "Abhi1",
  "userId": 3
}
```

#### 3. Get Leaderboard

**Endpoint:** `GET /api/contests/{id}/leaderboard`

**Description:** Fetches current leaderboard sorted by score

**Response Example:**

```json
[
  {
    "score": 200,
    "username": "Abhinav"
  },
  {
    "score": 0,
    "username": "Abhi1"
  }
]
```

#### 4. Update Score

**Endpoint:** `POST /api/contests/{id}/update-score`

**Description:** Updates user score after solving a problem (prevents duplicate scoring)

**Request Body:**

```json
{
  "userId": 2,
  "problemId": 1
}
```

**Response Example:**

```json
{
  "updated": true,
  "solvedProblems": [1],
  "newScore": 100
}
```

### Submission Endpoints

| Endpoint                | Method | Description               |
| ----------------------- | ------ | ------------------------- |
| `/api/submissions`      | POST   | Submit code for a problem |
| `/api/submissions/{id}` | GET    | Check submission verdict  |

#### 5. Submit Code

**Endpoint:** `POST /api/submissions`

**Description:** Submits code for evaluation and returns initial submission status

**Request Body:**

```json
{
  "user": { "id": 2 },
  "contest": { "id": 1 },
  "problem": { "id": 1 },
  "language": "cpp",
  "sourceCode": "#include <bits/stdc++.h>\nusing namespace std;\nint main() {\n    int a,b; \n    cin >> a >> b; \n    cout << (a+b);\n}"
}
```

**Response Example:**

```json
{
  "id": 7,
  "user": {
    "id": 2,
    "username": null
  },
  "contest": {
    "id": 1,
    "title": null,
    "code": null,
    "problems": null
  },
  "problem": {
    "id": 1,
    "title": null,
    "code": null,
    "statement": null,
    "language": null,
    "score": 0,
    "testCases": null
  },
  "sourceCode": "#include <bits/stdc++.h>\nusing namespace std;\nint main() {\n    int a,b; \n    cin >> a >> b; \n    cout << (a+b);\n}",
  "language": "cpp",
  "status": "RUNNING",
  "verdictMessage": null,
  "timeMs": null,
  "createdAt": "2025-10-26T04:13:43.135671333Z",
  "updatedAt": "2025-10-26T04:13:43.135672375Z"
}
```

#### 6. Get Submission Status

**Endpoint:** `GET /api/submissions/{id}`

**Description:** Polls submission status to check verdict (used for real-time updates)

**Response Example (After Evaluation):**

```json
{
  "id": 7,
  "user": {
    "id": 2,
    "username": null
  },
  "contest": {
    "id": 1,
    "title": "Shodh-a-Code Demo Contest",
    "code": "DEMO2025",
    "problems": [...]
  },
  "problem": {
    "id": 1,
    "title": "A + B Problem",
    "code": "ADD001",
    "statement": "Read two integers and output their sum.",
    "language": "java",
    "score": 100,
    "testCases": [...]
  },
  "sourceCode": "#include <bits/stdc++.h>\nusing namespace std;\nint main() {\n    int a,b; \n    cin >> a >> b; \n    cout << (a+b);\n}",
  "language": "cpp",
  "status": "ACCEPTED",
  "verdictMessage": "All testcases passed!",
  "timeMs": null,
  "createdAt": "2025-10-26T04:13:43.135671Z",
  "updatedAt": "2025-10-26T04:13:56.183723Z"
}
```

---

## Design Choices

### Backend Design

The backend is a **Spring Boot + JPA** based REST API that powers all contest logic - user registration, problem retrieval, submission handling, and leaderboard management. It also integrates a **Docker-based code execution engine (JudgeService)** to safely compile and execute user code inside isolated containers.

#### Architecture Overview

- **Spring Boot** provides the REST layer, dependency injection, and environment configuration
- **Spring Data JPA** handles ORM mappings and simplifies CRUD operations for all entities (Contest, Problem, Submission, etc.)
- **Lombok** reduces boilerplate via annotations like `@Builder`, `@Getter`, `@Setter`
- **Docker CLI integration** (via ProcessBuilder) enables dynamic creation of ephemeral language containers to compile and run code securely

#### Backend Project Structure

```
backend/
├── ShodhacodeApplication.java          # Entry point for Spring Boot
│
├── api/                                # REST Controllers (handles HTTP routes)
│   ├── ContestController.java          # Contest joining, leaderboard, update-score
│   ├── RootController.java             # Health check / base route
│   └── SubmissionController.java       # Code submission + status polling
│
├── bootstrap/
│   └── DataSeeder.java                 # Seeds initial contest, problems, and test cases
│
├── domain/                             # Data models (mapped to database tables)
│   ├── Contest.java                    # Represents a coding contest
│   ├── ContestParticipant.java         # Tracks user, score, and solved problems
│   ├── Problem.java                    # Individual problems with statements and scores
│   ├── Submission.java                 # User submissions and verdict data
│   ├── TestCase.java                   # Input/output test cases linked to each problem
│   └── UserAccount.java                # Basic user model (username and ID)
│
├── judge/                              # Code execution layer
│   └── JudgeService.java               # Handles compilation, execution, and result evaluation
│
├── repo/                               # Spring Data JPA Repositories
│   ├── ContestRepo.java
│   ├── ContestParticipantRepo.java
│   ├── ProblemRepo.java
│   ├── SubmissionRepo.java
│   ├── TestCaseRepo.java
│   └── UserRepo.java
│
└── resources/
    ├── application.properties          # DB + Docker config
    └── schema.sql / data.sql (if needed)
```

#### Key Backend Components

**1. JudgeService**

- Dynamically spawns Docker containers per submission
- Each submission executes in one of three language environments:
  - `gcc:13.2.0` for C++
  - `python:3.11-alpine` for Python
  - `azul/zulu-openjdk:17` for Java
- Uses `/tmp/judge_tmp/<uuid>` as a shared workspace between backend, host, and judge containers
- Ensures fair execution using limits:
  ```bash
  -m 256m   # memory limit
  --cpus=1  # CPU throttling
  timeout 3s # execution timeout
  ```

**2. ContestParticipant**

- Keeps a persistent record of:
  - Total score
  - Solved problem IDs (solvedProblems stored as comma-separated string)
- Provides helper methods:
  ```java
  getSolvedProblemSet() // parses solvedProblems into Set<Long>
  addSolvedProblem()    // updates solvedProblems string
  ```
- Prevents double-counting of solved problems

**3. ContestController**

- `/join` - creates or fetches user + participant record
- `/update-score` - updates participant's score only if the problem wasn't already solved
- `/leaderboard` - aggregates participant scores efficiently
- Clean separation between execution logic (JudgeService) and scoring logic (ContestController)

**4. SubmissionController**

- Handles submission POSTs from frontend
- Creates Submission records in DB
- Asynchronously invokes `JudgeService.runJudge()`
- Frontend polls `/submission/{id}` to get verdict updates

**5. DataSeeder**

- Seeds a sample contest with problems and test cases for testing
- Provides initial dataset for local runs or Docker demos

#### Docker-Based Judge Workflow

1. Backend creates a temp directory inside `/tmp/judge_tmp/<uuid>`
2. Writes the submitted source code there
3. Runs: `docker run -v /tmp/judge_tmp/<uuid>:/app gcc:13.2.0 sh -c "g++ main.cpp && ./a.out"`
4. Docker daemon executes it in a clean, isolated environment
5. Output is compared to expected test case output
6. Verdict (ACCEPTED, WA, TLE, etc.) is stored back in DB
7. Frontend polls and updates the result in real time

This model ensures **security, isolation, and deterministic results** regardless of local setup.

### Frontend Design

The frontend uses **React's built-in state** (`useState` / `useEffect`) and **Next.js's routing** for state management, avoiding unnecessary complexity.

#### State Management Approach

- Each contest page (`/contest/[contestId]`) loads its own state locally (contest data, problem, status)
- **Axios** handles all API synchronization, while local component state stores the current problem, verdict, and editor content
- Persistent data like username and userId are stored in **localStorage**, enabling users to rejoin the same contest session after refresh
- **Polling with Axios** keeps verdicts up-to-date in near real time, instead of maintaining global socket connections or external stores
- This approach keeps the UI responsive and isolates contest logic per route, ensuring components like `LeaderboardTable` and `ProblemPanel` stay modular and independent

#### Why This Approach?

- **Redux/Zustand would've added overhead** without major benefit for such a linear data flow
- **Native React state fits perfectly** for short-lived, scoped pages like contest sessions

### Docker Orchestration

#### Challenges Faced

**File Visibility Between Containers**

- The backend container initially created temp files inside its own isolated filesystem
- The host Docker daemon couldn't see those paths when mounting them into judge containers, causing "No such file or directory" and "mounts denied" errors

**Docker Socket Permissions & Mac File Sharing**

- On macOS, Docker Desktop uses a lightweight VM -> not all host paths (like `/judge_tmp`) are shared by default
- This required mounting a shared directory (`/tmp/judge_tmp`) and configuring Docker Desktop's File Sharing preferences

**Security vs Simplicity**

- Full sandboxing (via Firecracker or gVisor) would add isolation but increase complexity
- Using Docker containers for each execution provided enough isolation while keeping the setup lightweight and easy to replicate

### Scoring and Leaderboard

- Each problem has a fixed score
- On an accepted submission, `/update-score` updates the participant's score only if the problem has not been solved before
- The leaderboard reflects total scores stored in the participant record, ensuring consistency and efficiency

---

## Key Challenges

**Container Communication & Volume Sharing**

- The backend dynamically spawns isolated language containers (gcc, python, openjdk) to compile and run user code
- Initially, these containers couldn't access source files created by the backend, resulting in "No such file or directory" and mount errors
- The issue stemmed from using a non-whitelisted path (`/judge_tmp`) on macOS Docker Desktop
- It was resolved by switching to `/tmp/judge_tmp`, a system path already shared between the host and Docker, ensuring consistent cross-container access

**Docker Daemon Access**

- Since the backend itself runs in a Docker container, it initially had no permission to execute `docker run` commands
- This was fixed by mounting the host's Docker socket (`/var/run/docker.sock`) inside the backend container, allowing it to interact with the host daemon securely and launch judge containers on demand

**File System Permissions & Cleanup**

- The temporary execution directories (`/tmp/judge_tmp/<uuid>`) required correct write permissions and cleanup logic
- Each run now safely creates a unique temp folder, writes the user's code, executes all test cases, and recursively deletes the folder afterward to prevent residue buildup

**Preventing Double Scoring**

- Early versions allowed users to resubmit accepted code for unlimited score increments
- Introducing a `solvedProblems` field in `ContestParticipant` (storing comma-separated problem IDs) ensures a participant's score only increases the first time a problem is solved

---

## Project Structure

```
shodh-a-code/
├── backend/
│   ├── src/main/java/com/shodhai/shodhacode/
│   │   ├── api/               # REST Controllers
│   │   ├── domain/            # JPA Entities (Contest, Problem, Participant, etc.)
│   │   ├── repo/              # Repository Interfaces
│   │   ├── judge/             # Docker-based code executor
│   │   └── ShodhACodeApplication.java
│   ├── pom.xml
|   └── Dockerfile
│
├── frontend/
│   ├── src/app/contest/[id]/  # Contest routes
│   ├── components/            # Editor, ProblemPanel, Leaderboard, OutputBar
│   ├── lib/api.ts             # API utilities
│   ├── package.json
|   └── Dockerfile
│
├── docker-compose.yml         # Optional combined setup
└── README.md                  # Project documentation
```

---

## Key Things to Remember

### Port Conflicts (Manual Setup)

Before starting the application, ensure ports **8080** (backend) and **3000** (frontend) are not already in use:

```bash
# Check if port 8080 is in use
lsof -i :8080

# Check if port 3000 is in use
lsof -i :3000

# Kill process if needed (replace PID with actual process ID)
kill -9 <PID>
```

### Compilation or Runtime Errors for All Submissions

Make sure required Docker images are pulled locally:

```bash
docker pull gcc:13.2.0
docker pull azul/zulu-openjdk:17
docker pull python:3.11-alpine
```

---

## Authors

**Abhinav Chauhan**  
Full Stack Developer  
[LinkedIn](https://www.linkedin.com/in/abhinav-chauhan-639936253/)

---

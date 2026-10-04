# LankaConnect Customer Care System (CCMS)
**SE2030 – Software Engineering Group Project**  
**Group ID:** B5G1-10, Weekday, Malabe  

---

## 🌟 Overview
LankaConnect CCMS is an enterprise-grade, web-based customer care management platform built with **Java & Spring Boot** backend, **SQL** relational database, and a **modern light-themed responsive Web UI** (HTML5, Vanilla CSS3, JavaScript, Bootstrap 5, Chart.js).

---

## 👥 Six Major Functions & Team Allocation

| Member Name | Student ID | Scrum / Team Role | Major Function Owned |
| :--- | :--- | :--- | :--- |
| **Fernando T.S.V.** | IT25103514 | Developer 1 | **1. User Account Management & Loyalty Gamification** (Registration, Login, RBAC, Loyalty Badges for Customers, Activity Audit Log) |
| **Bogoda H.M.L.M.** | IT25101775 | Developer 2 | **2. Complaint Management** (Complaint CRUD, Department Routing, Auto-assignment, Resolution Status Tracking) |
| **Minsara M.D.M.** | IT25100722 | Developer 3 | **3. Live Chat Support** (Real-time Customer <-> Officer Chat, Active Queue, File Attachment, Chat Transfer) |
| **Denuwantha A.Y.** | IT25102607 | Product Owner | **4. Feedback Management & CSAT** (5-Star Ratings, CSAT % Computation, Automated Negative Feedback Follow-Up) |
| **Premasiri L.H.G.M.J.**| IT25101649 | Developer 4 | **5. Ticket Tracking & Status Updates** (Unique Ticket IDs `TICK-2026-XXXX`, SLA Overdue Alerts, Status History Audit) |
| **Dissanayake R.D.M.D.V.** | IT25103662 | Scrum Master | **6. Reporting, Monitoring & Spike Alerts** (Executive Dashboard, Trend Visualizations, **Automated Regional Spike Threshold Alerts**) |

---

## 🚀 How to Run the Application

### Option 1: 1-Click Batch Runner (Windows)
Double-click `run.bat` in the project root directory.

### Option 2: Command Line
```powershell
.\.maven\apache-maven-3.9.6\bin\mvn.cmd spring-boot:run
```

Once started, open your browser and navigate to:
👉 **`http://localhost:8080`**

---

## 🔑 Login Credentials & Demo Accounts

For rapid grading and demonstration, the login page features **1-Click Quick Login Demo Cards** for every role:

| Persona / Role | Username | Password | Key Capabilities |
| :--- | :--- | :--- | :--- |
| **Customer (Gold Tier)** | `customer1` | `123456` | Submit complaints, track tickets with live stepper, live chat with support officer, submit CSAT rating, view Gold loyalty badge. |
| **Customer (Silver Tier)** | `customer2` | `123456` | Submit complaints, view Silver loyalty badge, track tickets. |
| **Customer Service Officer** | `agent_kamal` | `123456` | Live chat command desk (multi-session queue), triage & assign complaints to department staff, transfer chat sessions. |
| **Department Staff (Tech)** | `staff_tech` | `123456` | Technical investigation queue, record root-cause resolution notes, update progress to Resolved/Closed. |
| **Operations Manager** | `manager` | `123456` | Regional spike threshold alerts, trend analytics, CSAT monitor, exportable reports. |
| **Customer Care Manager** | `cc_manager` | `123456` | CSAT breakdown metrics, negative feedback follow-up desk. |
| **System Administrator** | `admin` | `123456` | User account management, role & permission modification, security & activity audit logs. |

---

## 🛠️ Complete 4 CRUD Operations across All 6 Major Functions

As required for academic evaluation, every major function explicitly includes all 4 CRUD operations (**C**reate, **R**ead, **U**pdate, **D**elete):

| Major Function | Owned By | CREATE | READ | UPDATE | DELETE |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **1. User Account Management** | Fernando T.S.V. | `POST /api/auth/register`, `POST /api/users` | `GET /api/users`, `GET /api/users/{id}`, `GET /api/users/role/{role}` | `PUT /api/users/profile`, `PUT /api/users/manage/{id}`, `POST /api/users/reset-password` | `DELETE /api/users/{id}` |
| **2. Complaint Management** | Bogoda H.M.L.M. | `POST /api/complaints` | `GET /api/complaints`, `GET /api/complaints/{id}`, `GET /api/complaints/code/{code}` | `PUT /api/complaints/{id}`, `PUT /api/complaints/{id}/assign`, `PUT /api/complaints/{id}/status` | `DELETE /api/complaints/{id}` |
| **3. Live Chat Support** | Minsara M.D.M. | `POST /api/chat/start`, `POST /api/chat/send` | `GET /api/chat/messages/{sessionId}`, `GET /api/chat/queue`, `GET /api/chat/active` | `PUT /api/chat/messages/{messageId}`, `PUT /api/chat/sessions/{sessionId}`, `POST /api/chat/accept/{id}`, `POST /api/chat/transfer/{id}`, `POST /api/chat/close/{id}` | `DELETE /api/chat/sessions/{sessionId}`, `DELETE /api/chat/messages/{messageId}` |
| **4. Feedback Management** | Denuwantha A.Y. | `POST /api/feedback` | `GET /api/feedback`, `GET /api/feedback/{id}`, `GET /api/feedback/metrics`, `GET /api/feedback/negative-followups` | `PUT /api/feedback/{id}`, `PUT /api/feedback/{id}/followup` | `DELETE /api/feedback/{id}` |
| **5. Ticket Tracking & Updates** | Premasiri L.H.G.M.J. | `POST /api/tickets` | `GET /api/tickets`, `GET /api/tickets/{id}`, `GET /api/tickets/code/{code}`, `GET /api/tickets/search` | `PUT /api/tickets/{id}`, `PUT /api/tickets/{id}/status` | `DELETE /api/tickets/{id}` |
| **6. Reporting & Spike Alerts** | Dissanayake R.D.M.D.V. | `POST /api/dashboard/thresholds` | `GET /api/dashboard/stats`, `GET /api/dashboard/spike-alerts`, `GET /api/dashboard/thresholds` | `PUT /api/dashboard/thresholds/{id}` | `DELETE /api/dashboard/thresholds/{id}` |

---

## 🔍 Technical Staff Ticket Search Desk

For technical staff (`staff_tech`), a dedicated **Ticket Search & Inspection Desk** is provided on `staff-dashboard.html`:
- **Real-Time Ticket Search Bar**: Search tickets by Ticket Code (e.g., `TICK-2026-1001`), Complaint Ref Code, Customer Name, Title, Status, Priority, or Department (`GET /api/tickets/search?query=...`).
- **Filter & Management**: Instant status/priority filtering, SLA Due Date inspection, Overdue warnings, Audit History modal (`GET /api/tickets/{id}/history`), Create Ticket, Edit Ticket, and Delete Ticket actions.


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

## 🗄️ Database Information
- **Configured SQL Credentials**: Username: `sa`, Password: `dimuth2002`
- **Schema & DDL File**: Located in `src/main/resources/schema.sql`
- **H2 Database Web Console**: Accessible at `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:ccms_db`, User: `sa`, Password: `dimuth2002`)

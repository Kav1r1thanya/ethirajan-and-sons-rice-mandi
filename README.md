# 🌾 Ethirajan & Sons Rice Mandi — Retail ERP & POS System

[![Live Demo](https://img.shields.io/badge/Live-Demo-brightgreen?style=for-the-badge)](https://ethirajan-and-sons-rice-mandi.onrender.com)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x%20%2F%204.x-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0+-4479A1?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Docker](https://img.shields.io/badge/Docker-Containerized-2496ED?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/)

A full-stack retail management and Point-of-Sale (POS) ERP built to digitize daily ledger accounting, wholesale rice bag inventory, multi-tier credit tracking (khata), and retail billing for a commercial rice distribution business.

🔗 **Live Deployment:** [ethirajan-and-sons-rice-mandi.onrender.com](https://ethirajan-and-sons-rice-mandi.onrender.com)  
> *Note: Hosted on Render Free Tier. If the instance is sleeping, please allow 30–45 seconds for the initial wake-up call.*

📹 **Walkthrough Video is available in the repo**

---

## 🔑 Demo Access Credentials

To test Role-Based Access Control (RBAC) and evaluate live workflows:

| Role | Username | Password | Permitted Actions |
| :--- | :--- | :--- | :--- |
| **Admin** | `2310` | *(YourAdminPassword)* | Full ERP control: Ledger modifications, wholesale inventory, expense tracking, and business analytics. |
| **staff** | `5987` | *(YourStaffPassword)* | Restricted POS terminal: Customer billing and fast-dispatch counters only. |

---

## 📊 Viewing the Business Analytics & Visualizations

The ERP includes an administrative analytics dashboard tracking sales volume, profit margins, and daily expense breakdowns:

* **Recommended Filter:** When evaluating the dashboard charts, expense tracking and BI, please select **May 2026** in the date/month filter.
* **Why May 2026:** The application underwent its initial production trial and active operational testing during May 2026. This period contains complete wholesale orders, retail billing batches, and operating expense entries, providing an accurate demonstration of the data visualization and reporting components.

---

## ⚙️ Core Architecture & Features

* **Role-Based Operational Workflows (RBAC):** Distinct access levels ensuring store associates only access rapid billing screens, while business balance sheets, supplier credit ledgers, and profit statistics remain restricted to administrators.
* **Wholesale & Retail Stock Architecture:** Dual-unit inventory tracking handling standard wholesale rice bags (25kg, 26kg, 50kg) alongside loose retail kilogram sales with real-time stock deductions.
* **Credit & Debt Ledger:** Digital ledger (khata) system managing customer account debts, partial credit payments, and running transaction histories.
* **Production Deployment:** Containerized with Docker and connected to a persistent cloud MySQL database to ensure continuous data persistence.

---

## 🛠️ Tech Stack

* **Backend:** Java 21, Spring Boot, Spring Data JPA, Hibernate ORM
* **Database:** MySQL 8.x (Hosted via Aiven Cloud)
* **Frontend:** Thymeleaf, HTML5, CSS3, JavaScript
* **DevOps & Deployment:** Docker (Multi-stage build), Git, Render Cloud Platform

---

## 🏃 Local Setup Instructions

1. **Clone repository:**
   ```bash
   git clone [https://github.com/Kav1r1thanya/ethirajan-and-sons-rice-mandi.git](https://github.com/Kav1r1thanya/ethirajan-and-sons-rice-mandi.git)
   cd ethirajan-and-sons-rice-mandi

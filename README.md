# ⚡ High-Throughput E-Commerce & Event-Driven Flash Sale Engine

A distributed, high-concurrency e-commerce backend system built for massive traffic flash-sale events, ensuring data consistency and sub-second response times.

---

## 🎯 Architecture Goals
- **High Concurrency Handling:** Prevent overselling during flash sales with Redis atomic operations & distributed locks.
- **Event-Driven Architecture:** Asynchronous order fulfillment via Message Queues (Kafka / RabbitMQ).
- **Data Consistency:** Transactional Outbox Pattern & Saga Orchestration for order-payment transactions.
- **Production-Grade Observability:** Metrics, centralized logs, and distributed tracing.

---

## 🏗️ Project Structure
```text
├── docs/                 # Architecture designs, ERD diagrams, ADRs
├── deployments/          # Docker compose & Kubernetes manifests
├── src/                  # Main source code
└── tests/                # Load tests (k6), Integration tests
<script setup>
import { onMounted, ref } from "vue";

const apiBase =
  import.meta.env.VITE_API_URL ||
  "http://localhost:8280/api/medco-digital-parking/v1";

const fallbackData = {
  title: "Smart Parking System (SPS)",
  subtitle: "Meskel Square Parking Facility • Addis Ababa • Demo Procurement Portal",
  overview:
    "Capacity exceeds 1,400 vehicles across underground basements and surrounding lots. This demo showcases a secure, scalable, and fully integrated Smart Parking System for automated operations and revenue assurance.",
  stats: [
    { label: "Avg. Entry/Exit", value: "<2s" },
    { label: "LPR Accuracy", value: "≥98%" },
    { label: "Slots Covered", value: "1,400+" },
    { label: "Power Backup", value: "UPS + surge" },
  ],
  sections: [
    {
      title: "Core Features",
      items: [
        "Automated entry/exit with boom barriers and loop detectors",
        "Ticketless QR entry/exit via mobile app, web, or kiosks",
        "Real-time slot detection with zone guidance",
        "Digital signage at entry, levels, and exits",
        "User portal for booking, navigation, and cashless payment",
      ],
    },
    {
      title: "Operations & Analytics",
      items: [
        "Admin dashboard for occupancy, revenue, and audit trails",
        "Operator console for manual overrides and exception handling",
        "Custom reporting and export (Excel/PDF)",
        "Valet mode support with QR-based flows",
      ],
    },
    {
      title: "Integration",
      items: [
        "CCTV and access control integration",
        "Payment gateways (Telebirr, CBE Birr, bank cards)",
        "PMS data migration and API connectivity",
        "Ethiopian fiscal and taxation reporting compatibility",
      ],
    },
    {
      title: "Infrastructure",
      items: [
        "Hybrid cloud-local architecture with offline fallback",
        "TLS encryption with audit logging and 2FA",
        "Amharic + English accessibility",
        "UPS for control room, gates, and servers",
      ],
    },
  ],
  timeline: [
    { phase: "Mobilization", target: "Within 1 week of signing" },
    { phase: "Full Deployment", target: "≤45 days" },
    { phase: "Live Trial", target: "14–30 days before handover" },
  ],
  contact: {
    facility: "Meskel Square Parking Facility",
    address: "Addis Ababa, Ethiopia",
    email: "procurement@sps-demo.et",
    phone: "+251 11 000 0000",
  },
};

const demo = ref(fallbackData);
const loading = ref(true);
const error = ref(null);

const badges = [
  "ISO Certified Equipment",
  "GDPR-Equivalent Data Privacy",
  "Amharic + English UI",
  "Hybrid Cloud + Local",
];

onMounted(async () => {
  try {
    const response = await fetch(`${apiBase}/demo`);
    if (!response.ok) {
      throw new Error("Failed to load demo data");
    }
    demo.value = await response.json();
  } catch (err) {
    error.value = err?.message || "Demo data unavailable";
  } finally {
    loading.value = false;
  }
});
</script>

<template>
  <div class="sps-page">
    <header class="hero">
      <div class="hero-content">
        <p class="eyebrow">Smart Parking Procurement Demo • June 2025</p>
        <h1 class="hero-title">
          {{ demo.title }}
        </h1>
        <p class="hero-subtitle">
          {{ demo.subtitle }}
        </p>
        <p class="hero-overview">
          {{ demo.overview }}
        </p>
        <div class="hero-actions">
          <button class="primary-btn">Request Demo Walkthrough</button>
          <button class="ghost-btn">Download Specification PDF</button>
        </div>
        <div class="badge-row">
          <span v-for="badge in badges" :key="badge" class="badge">
            {{ badge }}
          </span>
        </div>
      </div>
      <div class="hero-panel">
        <div class="panel-card">
          <p class="panel-title">Live Status Snapshot</p>
          <div class="panel-grid">
            <div v-for="stat in demo.stats" :key="stat.label" class="stat-card">
              <p class="stat-value">{{ stat.value }}</p>
              <p class="stat-label">{{ stat.label }}</p>
            </div>
          </div>
          <div class="panel-footer">
            <p class="panel-note">
              Offline fallback enabled • TLS + audit logs • LPR + QR fusion
            </p>
          </div>
        </div>
        <div class="panel-card accent">
          <p class="panel-title">Operational Guarantees</p>
          <ul class="panel-list">
            <li>24/7 remote support with 4-hour onsite response.</li>
            <li>1-year full warranty, 3–5 year AMC options.</li>
            <li>14–30 day live trial before handover.</li>
          </ul>
        </div>
      </div>
    </header>

    <section class="section-grid">
      <div class="section-intro">
        <h2 class="section-title">Why Meskel Square Needs SPS</h2>
        <p class="section-text">
          The existing PMS suffers from manual ticketing, revenue leakage, and
          limited auditability. SPS automates access control, reduces congestion,
          and provides real-time visibility for operators and decision-makers.
        </p>
      </div>
      <div class="section-cards">
        <div class="mini-card">
          <p class="mini-title">Revenue Assurance</p>
          <p>Automated logging, payment tracking, and audit trails.</p>
        </div>
        <div class="mini-card">
          <p class="mini-title">Traffic Flow</p>
          <p>Guided parking, lane control, and smart signage.</p>
        </div>
        <div class="mini-card">
          <p class="mini-title">User Experience</p>
          <p>Advance booking, cashless payment, and push alerts.</p>
        </div>
      </div>
    </section>

    <section class="section-slab">
      <div class="slab-header">
        <h2 class="section-title">System Capabilities</h2>
        <p class="section-text">
          Core platform modules, operational intelligence, and integration-ready
          APIs designed for scalable growth.
        </p>
      </div>
      <div class="slab-grid">
        <div v-for="section in demo.sections" :key="section.title" class="slab-card">
          <h3 class="slab-title">{{ section.title }}</h3>
          <ul class="slab-list">
            <li v-for="item in section.items" :key="item">
              {{ item }}
            </li>
          </ul>
        </div>
      </div>
    </section>

    <section class="section-grid">
      <div class="section-intro">
        <h2 class="section-title">Hardware & Networking Stack</h2>
        <p class="section-text">
          Gate-ready LPR, loop detectors, kiosks, and LED signage with hybrid
          cloud-local architecture, secure TLS, and offline resilience.
        </p>
      </div>
      <div class="section-cards">
        <div class="mini-card">
          <p class="mini-title">Gate Hardware</p>
          <p>LPR cameras, boom barriers, QR scanners, and HD surveillance.</p>
        </div>
        <div class="mini-card">
          <p class="mini-title">Slot Sensors</p>
          <p>Ultrasonic or magnetic sensors with wireless transmission.</p>
        </div>
        <div class="mini-card">
          <p class="mini-title">Network & Power</p>
          <p>LAN + Wi-Fi, UPS-backed control room, surge protection.</p>
        </div>
      </div>
    </section>

    <section class="timeline">
      <div class="timeline-header">
        <h2 class="section-title">Implementation Timeline</h2>
        <p class="section-text">
          Fast deployment with staged commissioning and live trial validation.
        </p>
      </div>
      <div class="timeline-grid">
        <div v-for="item in demo.timeline" :key="item.phase" class="timeline-card">
          <p class="timeline-phase">{{ item.phase }}</p>
          <p class="timeline-target">{{ item.target }}</p>
        </div>
      </div>
    </section>

    <section class="section-slab compact">
      <div class="slab-header">
        <h2 class="section-title">Bid Evaluation Snapshot</h2>
        <p class="section-text">
          Technical compliance, local support capability, CAPEX/OPEX balance, and
          execution timeline are weighted in the evaluation framework.
        </p>
      </div>
      <div class="slab-grid single">
        <div class="slab-card">
          <ul class="slab-list">
            <li>Technical compliance with SPS specification.</li>
            <li>Experience delivering smart parking or ITS projects.</li>
            <li>Local presence for rapid onsite response.</li>
            <li>Implementation within ≤90 days preferred.</li>
          </ul>
        </div>
      </div>
    </section>

    <section class="contact">
      <div class="contact-card">
        <div>
          <h2 class="section-title">Contact & Submission</h2>
          <p class="section-text">
            Ready for a walkthrough or bid submission? Reach out to schedule a
            demo and receive the complete technical package.
          </p>
        </div>
        <div class="contact-details">
          <p class="contact-label">Facility</p>
          <p class="contact-value">{{ demo.contact.facility }}</p>
          <p class="contact-label">Address</p>
          <p class="contact-value">{{ demo.contact.address }}</p>
          <p class="contact-label">Email</p>
          <p class="contact-value">{{ demo.contact.email }}</p>
          <p class="contact-label">Phone</p>
          <p class="contact-value">{{ demo.contact.phone }}</p>
        </div>
      </div>
      <p v-if="error" class="error-text">
        {{ error }}. Showing local demo content.
      </p>
      <p v-if="loading" class="loading-text">Loading live demo data…</p>
    </section>
  </div>
</template>

<style scoped>
@import url("https://fonts.googleapis.com/css2?family=Fraunces:opsz,wght@9..144,600;9..144,700&family=Space+Grotesk:wght@400;500;600;700&display=swap");

.sps-page {
  color: #0f172a;
  background: radial-gradient(circle at top left, #e0f2fe 0%, #f8fafc 40%, #ffffff 100%);
  font-family: "Space Grotesk", sans-serif;
  min-height: 100vh;
}

.hero {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 2.5rem;
  padding: 4.5rem 6vw 3.5rem;
  position: relative;
  overflow: hidden;
}

.hero::after {
  content: "";
  position: absolute;
  width: 520px;
  height: 520px;
  background: radial-gradient(circle, rgba(14, 116, 144, 0.25), rgba(14, 116, 144, 0));
  top: -120px;
  right: -140px;
  z-index: 0;
}

.hero-content,
.hero-panel {
  position: relative;
  z-index: 1;
}

.eyebrow {
  text-transform: uppercase;
  letter-spacing: 0.2em;
  font-size: 0.7rem;
  color: #0f766e;
  font-weight: 600;
}

.hero-title {
  font-family: "Fraunces", serif;
  font-size: clamp(2.6rem, 4vw, 3.8rem);
  margin: 0.6rem 0 0.8rem;
  color: #0f172a;
}

.hero-subtitle {
  font-size: 1.1rem;
  font-weight: 600;
  color: #1e293b;
}

.hero-overview {
  margin-top: 1rem;
  font-size: 1rem;
  color: #334155;
  max-width: 560px;
}

.hero-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
  margin: 1.8rem 0;
}

.primary-btn,
.ghost-btn {
  border-radius: 999px;
  padding: 0.75rem 1.6rem;
  font-weight: 600;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.primary-btn {
  background: #0f766e;
  color: #f8fafc;
  box-shadow: 0 12px 30px rgba(15, 118, 110, 0.25);
}

.primary-btn:hover,
.ghost-btn:hover {
  transform: translateY(-2px);
}

.ghost-btn {
  background: #e2e8f0;
  color: #0f172a;
}

.badge-row {
  display: flex;
  flex-wrap: wrap;
  gap: 0.6rem;
}

.badge {
  background: #ecfeff;
  color: #155e75;
  padding: 0.4rem 0.9rem;
  border-radius: 999px;
  font-size: 0.75rem;
  font-weight: 600;
}

.hero-panel {
  display: grid;
  gap: 1.5rem;
}

.panel-card {
  background: #ffffff;
  border-radius: 1.5rem;
  padding: 1.8rem;
  box-shadow: 0 24px 50px rgba(15, 23, 42, 0.08);
}

.panel-card.accent {
  background: #0f172a;
  color: #e2e8f0;
}

.panel-title {
  font-weight: 700;
  font-size: 1rem;
  margin-bottom: 1rem;
}

.panel-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
  gap: 1rem;
}

.stat-card {
  background: #f1f5f9;
  border-radius: 1rem;
  padding: 1rem;
}

.panel-card.accent .stat-card {
  background: rgba(255, 255, 255, 0.08);
}

.stat-value {
  font-size: 1.4rem;
  font-weight: 700;
  color: #0f766e;
}

.panel-card.accent .stat-value {
  color: #5eead4;
}

.stat-label {
  font-size: 0.8rem;
  color: #475569;
}

.panel-card.accent .stat-label {
  color: #cbd5f5;
}

.panel-footer {
  margin-top: 1rem;
}

.panel-note {
  font-size: 0.85rem;
  color: #64748b;
}

.panel-card.accent .panel-note {
  color: rgba(226, 232, 240, 0.7);
}

.panel-list {
  display: grid;
  gap: 0.8rem;
  font-size: 0.95rem;
}

.section-grid,
.timeline,
.section-slab,
.contact {
  padding: 3.5rem 6vw;
}

.section-grid {
  display: grid;
  gap: 2rem;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
}

.section-intro {
  max-width: 480px;
}

.section-title {
  font-family: "Fraunces", serif;
  font-size: 2rem;
  margin-bottom: 1rem;
}

.section-text {
  color: #475569;
  font-size: 1rem;
}

.section-cards {
  display: grid;
  gap: 1rem;
}

.mini-card {
  background: #ffffff;
  border-radius: 1.25rem;
  padding: 1.4rem;
  box-shadow: 0 12px 30px rgba(15, 23, 42, 0.06);
}

.mini-title {
  font-weight: 700;
  margin-bottom: 0.5rem;
}

.section-slab {
  background: #0f172a;
  color: #e2e8f0;
}

.section-slab.compact {
  background: #0f766e;
}

.slab-header {
  max-width: 560px;
  margin-bottom: 2rem;
}

.slab-grid {
  display: grid;
  gap: 1.5rem;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
}

.slab-grid.single {
  grid-template-columns: 1fr;
  max-width: 520px;
}

.slab-card {
  background: rgba(255, 255, 255, 0.08);
  border-radius: 1.25rem;
  padding: 1.6rem;
}

.slab-title {
  font-weight: 700;
  margin-bottom: 0.8rem;
  font-size: 1.1rem;
}

.slab-list {
  display: grid;
  gap: 0.6rem;
  color: #e2e8f0;
}

.timeline {
  background: linear-gradient(120deg, #f8fafc 0%, #ecfeff 100%);
}

.timeline-grid {
  display: grid;
  gap: 1rem;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  margin-top: 2rem;
}

.timeline-card {
  background: #ffffff;
  border-radius: 1.25rem;
  padding: 1.4rem;
  box-shadow: 0 18px 40px rgba(15, 23, 42, 0.08);
}

.timeline-phase {
  font-weight: 700;
  font-size: 1rem;
}

.timeline-target {
  color: #0f766e;
  margin-top: 0.4rem;
}

.contact-card {
  background: #ffffff;
  border-radius: 1.5rem;
  padding: 2rem;
  display: grid;
  gap: 2rem;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  box-shadow: 0 20px 40px rgba(15, 23, 42, 0.08);
}

.contact-details {
  display: grid;
  gap: 0.4rem;
}

.contact-label {
  font-size: 0.75rem;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  color: #94a3b8;
}

.contact-value {
  font-weight: 600;
}

.error-text,
.loading-text {
  margin-top: 1.2rem;
  color: #64748b;
}

@media (max-width: 640px) {
  .hero {
    padding-top: 3rem;
  }

  .hero-actions {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>

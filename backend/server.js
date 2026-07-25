const express = require('express');
const cors = require('cors');
require('dotenv').config();

const app = express();
const PORT = process.env.PORT || 5000;

app.use(cors());
app.use(express.json());

// Sample in-memory database for VPS nodes & payments
const vpsNodes = [
  {
    id: "srv_sg_01",
    name: "Singapore Ultra-Fast VLESS",
    region: "Singapore",
    countryCode: "SG",
    protocol: "VLESS",
    address: "sg1.v2ray-secure.com",
    port: 443,
    uuid: "7c9e6679-7425-40de-944b-e07fc1f90ae7",
    network: "ws",
    path: "/vless-ws",
    security: "tls",
    isPremium: false,
    pingMs: 38
  },
  {
    id: "srv_us_reality",
    name: "USA Reality Stealth Node",
    region: "United States",
    countryCode: "US",
    protocol: "Reality",
    address: "us-west.v2ray-secure.com",
    port: 443,
    uuid: "b92138a0-2f9a-4c28-98e3-4712028c9f0b",
    network: "tcp",
    security: "reality",
    sni: "www.apple.com",
    publicKey: "q8R27346vNkY281B94n1zMkN3L9",
    flow: "xtls-rprx-vision",
    isPremium: true,
    pingMs: 145
  }
];

const pendingPayments = [
  {
    id: "pay_1092",
    userName: "Alex Rivera",
    method: "KBZ Pay",
    planName: "1 Month VIP",
    amount: "15,000 MMK",
    transactionId: "202607251029384",
    status: "PENDING",
    timestamp: Date.now()
  }
];

// Routes
app.get('/api/health', (req, res) => {
  res.json({ status: 'ok', service: 'Secure V2Ray Backend API', timestamp: new Date() });
});

// Get Servers List
app.get('/api/servers', (req, res) => {
  res.json({ success: true, servers: vpsNodes });
});

// Add New Server
app.post('/api/admin/servers', (req, res) => {
  const newServer = req.body;
  newServer.id = 'srv_' + Date.now();
  vpsNodes.push(newServer);
  res.json({ success: true, server: newServer });
});

// Submit Payment
app.post('/api/payments/submit', (req, res) => {
  const { userName, method, planName, amount, transactionId } = req.body;
  const payment = {
    id: 'pay_' + Date.now(),
    userName: userName || 'Anonymous',
    method,
    planName,
    amount,
    transactionId,
    status: 'PENDING',
    timestamp: Date.now()
  };
  pendingPayments.push(payment);
  res.json({ success: true, message: 'Payment submitted for review', payment });
});

// Get All Payments (Admin)
app.get('/api/admin/payments', (req, res) => {
  res.json({ success: true, payments: pendingPayments });
});

// Approve Payment
app.post('/api/admin/payments/:id/approve', (req, res) => {
  const { id } = req.params;
  const pay = pendingPayments.find(p => p.id === id);
  if (pay) {
    pay.status = 'APPROVED';
    return res.json({ success: true, payment: pay });
  }
  res.status(404).json({ success: false, message: 'Payment not found' });
});

app.listen(PORT, () => {
  console.log(`[Secure V2Ray Backend] API server running on port ${PORT}`);
});

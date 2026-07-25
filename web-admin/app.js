const initialServers = [
  { name: "Singapore Ultra-Fast VLESS", protocol: "VLESS", address: "sg1.v2ray-secure.com:443", region: "Singapore", type: "Free", status: "Online" },
  { name: "Tokyo High-Speed VMess", protocol: "VMess", address: "jp1.v2ray-secure.com:443", region: "Japan", type: "Free", status: "Online" },
  { name: "USA Reality Stealth Node", protocol: "Reality", address: "us-west.v2ray-secure.com:443", region: "United States", type: "VIP", status: "Online" },
  { name: "Myanmar Gaming Express", protocol: "VLESS", address: "mm-vip.v2ray-secure.com:443", region: "Myanmar VIP", type: "VIP", status: "Online" }
];

let initialPayments = [
  { id: "pay_1092", user: "Alex Rivera", method: "KBZ Pay", plan: "1 Month VIP", amount: "15,000 MMK", txid: "202607251029384", status: "PENDING" }
];

function renderServers() {
  const tbody = document.getElementById("servers-table-body");
  tbody.innerHTML = initialServers.map(s => `
    <tr>
      <td><strong>${s.name}</strong></td>
      <td><span style="color: #8b5cf6; font-weight: bold;">${s.protocol}</span></td>
      <td>${s.address}</td>
      <td>${s.region}</td>
      <td>${s.type}</td>
      <td><span style="color: #10b981; font-weight: bold;">● ${s.status}</span></td>
    </tr>
  `).join("");
}

function renderPayments() {
  const tbody = document.getElementById("payments-table-body");
  tbody.innerHTML = initialPayments.map(p => `
    <tr>
      <td><strong>${p.user}</strong></td>
      <td>${p.method}</td>
      <td>${p.plan}</td>
      <td>${p.amount}</td>
      <td><code>${p.txid}</code></td>
      <td><span style="color: ${p.status === 'APPROVED' ? '#10b981' : '#f59e0b'}">${p.status}</span></td>
      <td>
        ${p.status === 'PENDING' ? `
          <button class="btn btn-success" onclick="approvePayment('${p.id}')">Approve</button>
        ` : '<span>Done</span>'}
      </td>
    </tr>
  `).join("");
}

function approvePayment(id) {
  const p = initialPayments.find(x => x.id === id);
  if (p) {
    p.status = 'APPROVED';
    document.getElementById("pending-count").textContent = initialPayments.filter(x => x.status === 'PENDING').length;
    renderPayments();
  }
}

function addNodePrompt() {
  const name = prompt("Enter VPS Node Name:");
  if (name) {
    initialServers.push({
      name: name,
      protocol: "VLESS",
      address: "custom.v2ray-secure.com:443",
      region: "Custom",
      type: "VIP",
      status: "Online"
    });
    renderServers();
  }
}

document.addEventListener("DOMContentLoaded", () => {
  renderServers();
  renderPayments();
});

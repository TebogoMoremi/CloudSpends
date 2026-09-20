import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
} from "recharts";

function CostBreakdownChart({ breakdown, currency }) {
  const data = breakdown.map((item) => ({
    resourceType: item.resourceType,
    cost: Number(item.cost),
  }));

  if (data.length === 0) {
    return (
      <div className="chart-empty">
        No cost data available for this period.
      </div>
    );
  }

  return (
    <div className="chart-container">
      <ResponsiveContainer width="100%" height={320}>
        <BarChart
          data={data}
          margin={{
            top: 20,
            right: 20,
            left: 0,
            bottom: 10,
          }}
        >
          <CartesianGrid
            strokeDasharray="3 3"
            stroke="#1f2937"
            vertical={false}
          />

          <XAxis
            dataKey="resourceType"
            stroke="#64748b"
            tick={{ fill: "#94a3b8" }}
          />

          <YAxis
            stroke="#64748b"
            tick={{ fill: "#94a3b8" }}
          />

          <Tooltip
            cursor={{ fill: "#172033" }}
            contentStyle={{
              background: "#111827",
              border: "1px solid #334155",
              borderRadius: "8px",
              color: "#ffffff",
            }}
            formatter={(value) => [
              `${currency} ${Number(value).toFixed(2)}`,
              "Cost",
            ]}
          />

          <Bar
            dataKey="cost"
            fill="#3b82f6"
            radius={[6, 6, 0, 0]}
          />
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
}

export default CostBreakdownChart;
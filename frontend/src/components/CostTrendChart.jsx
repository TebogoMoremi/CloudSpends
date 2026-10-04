import {
  Area,
  AreaChart,
  CartesianGrid,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from "recharts";

function CostTrendChart({ dailyCosts, currency = "USD" }) {
  if (!dailyCosts || dailyCosts.length === 0) {
    return (
      <div className="state-message">
        No cost trend data available.
      </div>
    );
  }

  const chartData = dailyCosts.map((item) => ({
    ...item,
    cost: Number(item.cost),
    label: new Date(`${item.date}T00:00:00`).toLocaleDateString(
      "en-ZA",
      {
        day: "2-digit",
        month: "short",
      }
    ),
  }));

  return (
    <div style={{ width: "100%", height: "320px" }}>
      <ResponsiveContainer width="100%" height="100%">
        <AreaChart
          data={chartData}
          margin={{
            top: 20,
            right: 20,
            left: 0,
            bottom: 10,
          }}
        >
          <CartesianGrid strokeDasharray="3 3" />

          <XAxis
            dataKey="label"
            minTickGap={25}
          />

          <YAxis
            tickFormatter={(value) =>
              `$${Number(value).toFixed(0)}`
            }
          />

          <Tooltip
            formatter={(value) => [
              `${currency} ${Number(value).toFixed(2)}`,
              "Cost",
            ]}
            labelFormatter={(label) => `Date: ${label}`}
          />

          <Area
            type="monotone"
            dataKey="cost"
            name="Daily Cost"
            stroke="#60a5fa"
            fill="#60a5fa"
            fillOpacity={0.18}
            strokeWidth={2}
          />
        </AreaChart>
      </ResponsiveContainer>
    </div>
  );
}

export default CostTrendChart;
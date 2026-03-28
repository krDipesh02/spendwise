import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

const backendTarget = "http://localhost:8080";
const backendBasePath = "/api/v1";

function createProxyConfig() {
  return {
    target: backendTarget,
    changeOrigin: true,
    rewrite: (path) => `${backendBasePath}${path}`
  };
}

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      "/api/v1": {
        target: backendTarget,
        changeOrigin: true
      }
    }
  }
});

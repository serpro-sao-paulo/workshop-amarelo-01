import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "SIFAP",
  description: "Sistema de Fiscalização e Administração de Pagamentos",
};

export function RootLayout({
  children,
}: Readonly<{ children: React.ReactNode }>) {
  return (
    <html lang="pt-BR">
      <body>{children}</body>
    </html>
  );
}

export default RootLayout;

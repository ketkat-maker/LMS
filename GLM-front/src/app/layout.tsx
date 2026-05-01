import type {Metadata} from "next";
import {Geist, Geist_Mono} from "next/font/google";
import "./globals.css";
import {Toaster} from "@/components/ui/toaster";

const geistSans = Geist({
    variable: "--font-geist-sans",
    subsets: ["latin"],
});

const geistMono = Geist_Mono({
    variable: "--font-geist-mono",
    subsets: ["latin"],
});

export const metadata: Metadata = {
    title: "LMS - Learning Management System",
    description: "A modern Learning Management System for students, instructors, and administrators. Built with Next.js, TypeScript, and shadcn/ui.",
    keywords: ["LMS", "Learning Management System", "Education", "Next.js", "TypeScript", "shadcn/ui"],
    authors: [{name: "LMS Team"}],
    icons: {
        icon: "https://z-cdn.chatglm.cn/z-ai/static/logo.svg",
    },
    openGraph: {
        title: "LMS - Learning Management System",
        description: "A modern Learning Management System for education",
        type: "website",
    },
};

export default function RootLayout({
                                       children,
                                   }: Readonly<{
    children: React.ReactNode;
}>) {
    return (
        <html lang="en" suppressHydrationWarning>
        <body
            className={`${geistSans.variable} ${geistMono.variable} antialiased bg-background text-foreground`}
        >
        {children}
        <Toaster/>
        </body>
        </html>
    );
}

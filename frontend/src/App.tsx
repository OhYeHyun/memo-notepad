import React, { useEffect, useState } from "react";
import LoginScreen from "./LoginScreen";
import LoginModal from "./components/LoginModal";
import SignupModal from "./components/SignupModal";
import MemoScreen, { Me } from "./MemoScreen";
import { j } from "./api";
import Header from "./Header";

export default function App() {
    const [me, setMe] = useState<Me | null>(null);
    const [loading, setLoading] = useState(true);
    const [loginOpen, setLoginOpen] = useState(false);
    const [signupOpen, setSignupOpen] = useState(false);
    const [sessionRev, setSessionRev] = useState(0);

    const bump = () => setSessionRev((r) => r + 1);
    let meAbort: AbortController | null = null;

    const fetchMe = async () => {
        meAbort?.abort();
        meAbort = new AbortController();
        try {
            const next = await j<Me>("/api/auth/me", {
                method: "GET",
                credentials: "include",
                cache: "no-store",
                signal: meAbort.signal
            });
            setMe((prev) => {
                const changed =
                    !prev ||
                    prev.role !== next.role ||
                    prev.name !== next.name;
                if (changed) bump();
                return next;
            });
        } catch {
            setMe((prev) => {
                if (prev !== null) bump();
                return null;
            });
        } finally {
            meAbort = null;
        }
    };

    if (typeof window !== "undefined") {
        window.addEventListener("pageshow", e => {
            if ((e as PageTransitionEvent).persisted) {
                fetchMe();
            }
        });
        document.addEventListener("visibilitychange", () => {
            if (document.visibilityState === "visible") fetchMe();
        });
    }

    useEffect(() => {
        (async () => {
            setLoading(true);
            await fetchMe();
            setLoading(false);
        })();
    }, []);

    const guest = async () => {
        try { await j<void>("/api/auth/guest", { method: "POST" }); await fetchMe(); }
        catch (e) { console.error("게스트 로그인 실패:", e); }
    };

    const logout = async () => {
        try {
            await j<void>("/api/auth/logout", { method: "POST" });
        } catch (e) {
            console.error("로그아웃 실패:", e);
        } finally {
            setMe(null);
            bump();
        }
    };

    if (loading) {
        return (
            <div style={{display:"flex",justifyContent:"center",alignItems:"center",height:"100vh",color:"#9aa3b2"}}>
                로딩 중...
            </div>
        );
    }

    const isLoginScreen = !me;

    return (
        <>
            <Header
                me={me}
                onLogout={logout}
                onOpenLogin={() => setLoginOpen(true)}
                onOpenSignup={() => setSignupOpen(true)}
                hideActions={isLoginScreen}
            />

            {isLoginScreen ? (
                <LoginScreen
                    onGuest={guest}
                    onLocalLoginClick={() => setLoginOpen(true)}
                    onOpenSignup={() => setSignupOpen(true)}
                />
            ) : (
                <MemoScreen key={`session-${sessionRev}`} me={me!} onLogout={logout}/>
            )}

            <LoginModal open={loginOpen} onClose={() => setLoginOpen(false)} onSuccess={fetchMe} />
            <SignupModal open={signupOpen} onClose={()=>setSignupOpen(false)} onSuccess={fetchMe}/>
        </>
    );
}

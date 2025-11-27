import React, { useCallback, useEffect, useRef, useState } from "react";
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
    const [, setSessionRev] = useState(0);

    const bump = () => setSessionRev((r) => r + 1);

    const fetchingRef = useRef(false);
    const abortRef = useRef<AbortController | null>(null);
    const lastFetchAtRef = useRef(0);
    const unmountedRef = useRef(false);

    const COOLDOWN_MS = 1000;
    const POLL_MS = 30000;

    const safeSetMe = useCallback((updater: (prev: Me | null) => Me | null) => {
        if (!unmountedRef.current) setMe(updater);
    }, []);

    const fetchMe = useCallback(async () => {
        const now = Date.now();

        if (now - lastFetchAtRef.current < COOLDOWN_MS) return;
        lastFetchAtRef.current = now;

        if (fetchingRef.current) return;
        fetchingRef.current = true;

        abortRef.current?.abort();
        abortRef.current = new AbortController();

        try {
            const next = await j<Me>("/api/auth/me", {
                method: "GET",
                credentials: "include",
                cache: "no-store",
                signal: abortRef.current.signal,
            });

            safeSetMe(() => next);
        } catch {
            safeSetMe(() => null);
        } finally {
            abortRef.current = null;
            fetchingRef.current = false;
        }
    }, [safeSetMe]);

    useEffect(() => {
        unmountedRef.current = false;
        (async () => {
            setLoading(true);
            await fetchMe();
            setLoading(false);
        })();
        return () => {
            unmountedRef.current = true;
            abortRef.current?.abort();
        };
    }, [fetchMe]);

    useEffect(() => {
        const onVisible = () => {
            if (document.visibilityState === "visible") {
                void fetchMe();
            }
        };
        document.addEventListener("visibilitychange", onVisible);
        return () => document.removeEventListener("visibilitychange", onVisible);
    }, [fetchMe]);

    useEffect(() => {
        const id = setInterval(() => {
            if (!document.hidden) {
                void fetchMe();
            }
        }, POLL_MS);

        return () => clearInterval(id);
    }, [fetchMe]);

    const guest = useCallback(async () => {
        try {
            await j<void>("/api/auth/guest", { method: "POST" });
            await fetchMe();
            bump();
        } catch (e) {
            console.error("게스트 로그인 실패:", e);
        }
    }, [fetchMe]);

    const logout = useCallback(async () => {
        try {
            await j<void>("/api/auth/logout", { method: "POST" });
        } catch (e) {
            console.error("로그아웃 실패:", e);
        } finally {
            safeSetMe(() => null);
            bump();
        }
    }, [safeSetMe]);

    if (loading) {
        return (
            <div style={{
                display:"flex",
                justifyContent:"center",
                alignItems:"center",
                height:"100vh",
                color:"#9aa3b2"
            }}>
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
                <MemoScreen key="memo-screen" me={me!} onLogout={logout} />
            )}

            <LoginModal open={loginOpen} onClose={() => setLoginOpen(false)} onSuccess={fetchMe} />
            <SignupModal open={signupOpen} onClose={() => setSignupOpen(false)} onSuccess={fetchMe} />
        </>
    );
}

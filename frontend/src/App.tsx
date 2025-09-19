import React, { useEffect, useState } from "react";
import LoginScreen from "./LoginScreen";
import LoginModal from "./components/LoginModal";
import MemoScreen, { Me } from "./MemoScreen";
import { j } from "./api";
import Header from "./Header";

export default function App() {
    const [me, setMe] = useState<Me | null>(null);
    const [loading, setLoading] = useState(true);
    const [loginOpen, setLoginOpen] = useState(false);

    const fetchMe = async () => {
        try { setMe(await j<Me>("/api/auth/me")); } catch { setMe(null); }
    };

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
        try { await j<void>("/api/auth/logout", { method: "POST" }); setMe(null); }
        catch (e) { console.error("로그아웃 실패:", e); }
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
                hideActions={isLoginScreen}
            />

            {isLoginScreen ? (
                <LoginScreen onGuest={guest} onLocalLoginClick={() => setLoginOpen(true)} />
            ) : (
                <MemoScreen me={me!} onLogout={logout} onOpenLogin={() => setLoginOpen(true)} />
            )}

            <LoginModal open={loginOpen} onClose={() => setLoginOpen(false)} onSuccess={fetchMe} />
        </>
    );
}

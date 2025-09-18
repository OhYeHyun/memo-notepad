import { useEffect, useState } from "react";
import LoginScreen from "./LoginScreen";
import LoginModal from "./components/LoginModal";
import MemoScreen, { Me } from "./MemoScreen";

async function j<T>(url: string, init?: RequestInit): Promise<T> {
    const res = await fetch(url, {
        credentials: "include",
        headers: { "Content-Type": "application/json" },
        ...init,
    });
    if (res.status === 204) return undefined as T;
    if (!res.ok) throw new Error(await res.text().catch(() => res.statusText));
    return res.json();
}

export default function App() {
    const [me, setMe] = useState<Me | null>(null);
    const [loading, setLoading] = useState(true);
    const [loginOpen, setLoginOpen] = useState(false);

    const fetchMe = async () => {
        try {
            const data = await j<Me>("/api/auth/me");
            setMe(data);
        } catch {
            setMe(null);
        }
    };

    useEffect(() => {
        (async () => {
            setLoading(true);
            await fetchMe();
            setLoading(false);
        })();
    }, []);

    const guest = async () => {
        await j<void>("/api/auth/guest", { method: "POST" });
        await fetchMe();
    };

    const logout = async () => {
        await j<void>("/api/auth/logout", { method: "POST" });
        setMe(null);
    };

    if (loading) return null;

    return me ? (
        <>
            <MemoScreen me={me} onLogout={logout} />
        </>
    ) : (
        <>
            <LoginScreen onGuest={guest} onLocalLoginClick={() => setLoginOpen(true)} />
            <LoginModal open={loginOpen} onClose={() => setLoginOpen(false)} onSuccess={fetchMe} />
        </>
    );
}

import { useState } from "react";

type Props = {
    open: boolean;
    onClose: () => void;
    onSuccess: () => Promise<void>; // 로그인 성공 후 me 재조회 콜백
};

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

export default function LoginModal({ open, onClose, onSuccess }: Props) {
    const [loginId, setLoginId] = useState("");
    const [password, setPassword] = useState("");
    const [busy, setBusy] = useState(false);
    const [err, setErr] = useState<string | null>(null);

    if (!open) return null;

    const submit = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            setBusy(true);
            setErr(null);
            await j<void>("/api/auth/login", {
                method: "POST",
                body: JSON.stringify({ loginId, password }),
            });
            await onSuccess(); // /api/auth/me 재조회 → 상위 상태 전환
            onClose();
        } catch (e: any) {
            setErr(e.message ?? "로그인 실패");
        } finally {
            setBusy(false);
        }
    };

    return (
        <div className="modal-backdrop" onClick={onClose}>
            <div className="modal" onClick={(e) => e.stopPropagation()}>
                <h3>아이디/비밀번호 로그인</h3>
                <form onSubmit={submit} className="form-col">
                    <input
                        className="input"
                        placeholder="아이디"
                        value={loginId}
                        onChange={(e) => setLoginId(e.target.value)}
                        autoFocus
                    />
                    <input
                        className="input"
                        type="password"
                        placeholder="비밀번호"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                    />
                    {err && <div className="error">{err}</div>}

                    <div className="row gap">
                        <button className="btn primary" disabled={busy}>
                            {busy ? "로그인 중..." : "로그인"}
                        </button>
                        <button className="btn ghost" type="button" onClick={onClose}>
                            닫기
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}

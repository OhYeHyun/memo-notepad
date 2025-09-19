import React, { useState } from "react";
import { j } from "../api";

type Props = { open: boolean; onClose: () => void; onSuccess: () => Promise<void>;};

export default function LoginModal({ open, onClose, onSuccess }: Props) {
    const [loginId, setLoginId] = useState("");
    const [password, setPassword] = useState("");
    const [busy, setBusy] = useState(false);
    const [err, setErr] = useState<string | null>(null);
    if (!open) return null;

    const submit = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            setBusy(true); setErr(null);
            await j<void>("/api/auth/login", { method: "POST", body: JSON.stringify({ loginId, password }) });
            await onSuccess(); onClose();
        } catch (e: any) { setErr(e.message ?? "로그인 실패"); }
        finally { setBusy(false); }
    };

    return (
        <div className="modal-backdrop" onClick={onClose}>
            <div className="modal" onClick={(e) => e.stopPropagation()}>
                <h3 style={{ marginTop: 0 }}>아이디/비밀번호 로그인</h3>

                <form onSubmit={submit} className="form-col">
                    <input className="input" placeholder="아이디" value={loginId} onChange={(e) => setLoginId(e.target.value)} autoFocus />
                    <input className="input" type="password" placeholder="비밀번호" value={password} onChange={(e) => setPassword(e.target.value)} />
                    {err && <div className="error">{err}</div>}

                    <div className="row gap">
                        <button className="btn primary lg" disabled={busy}>{busy ? "로그인 중..." : "로그인"}</button>
                        <button className="btn ghost lg" type="button" onClick={onClose}>닫기</button>
                    </div>
                </form>

                <div className="alt-auth">
                    <a className="btn kakao lg" href="/oauth2/authorization/kakao">카카오 로그인</a>
                </div>
            </div>
        </div>
    );
}

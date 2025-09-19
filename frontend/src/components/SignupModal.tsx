import { useState } from "react";
import { j } from "../api";

type Props = { open: boolean; onClose: () => void; onSuccess?: () => Promise<void>; };

export default function SignupModal({ open, onClose, onSuccess }: Props) {
    const [name, setName] = useState("");
    const [loginId, setLoginId] = useState("");
    const [password, setPassword] = useState("");
    const [password2, setPassword2] = useState("");
    const [busy, setBusy] = useState(false);
    const [err, setErr] = useState<string | null>(null);
    if (!open) return null;

    const submit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!name.trim() || !loginId.trim() || !password) { setErr("모든 항목을 입력해 주세요."); return; }
        if (password !== password2) { setErr("비밀번호가 일치하지 않습니다."); return; }
        try {
            setBusy(true); setErr(null);
            await j<void>("/api/auth/signup", { method:"POST", body: JSON.stringify({ name: name.trim(), loginId: loginId.trim(), password }) });
            if (onSuccess) await onSuccess();
            onClose();
        } catch (e:any) { setErr("회원가입 실패"); }
        finally { setBusy(false); }
    };

    return (
        <div className="modal-backdrop" onClick={onClose}>
            <div className="modal" onClick={(e) => e.stopPropagation()}>
                <h3 style={{marginTop:0}}>회원가입</h3>
                <form className="form-col" onSubmit={submit}>
                    <input className="input" placeholder="이름" value={name} onChange={e=>setName(e.target.value)} autoFocus />
                    <input className="input" placeholder="아이디" value={loginId} onChange={e=>setLoginId(e.target.value)} />
                    <input className="input" type="password" placeholder="비밀번호" value={password} onChange={e=>setPassword(e.target.value)} />
                    <input className="input" type="password" placeholder="비밀번호 확인" value={password2} onChange={e=>setPassword2(e.target.value)} />
                    {err && <div className="error">{err}</div>}
                    <div className="row gap">
                        <button className="btn primary" disabled={busy}>{busy ? "가입 중..." : "가입하기"}</button>
                        <button className="btn ghost" type="button" onClick={onClose}>닫기</button>
                    </div>
                </form>
            </div>
        </div>
    );
}

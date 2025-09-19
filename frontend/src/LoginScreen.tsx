import React from "react";

type Props = {
    onGuest: () => Promise<void>;
    onLocalLoginClick: () => void;
    onOpenSignup: () => void;
};

export default function LoginScreen({ onGuest, onLocalLoginClick, onOpenSignup }: Props) {
    return (
        <div className="login-hero">
            <div className="login-card">
                <div className="brand brand--gradient">Notepad</div>
                <p>간단하고 빠른 메모</p>

                <div className="btns" style={{ justifyContent: "center" }}>
                    <button className="btn primary lg" onClick={onLocalLoginClick}>시작하기</button>
                    <button className="btn ghost lg" onClick={onGuest}>게스트로 사용해보기</button>
                </div>

                <div className="signup-cta" style={{marginTop:12, display:"flex", gap:8, alignItems:"center", justifyContent:"center"}}>
                    <span className="muted">아직 계정이 없나요?</span>
                    <button className="btn linklike" onClick={onOpenSignup}>회원가입</button>
                </div>
            </div>
        </div>
    );
}

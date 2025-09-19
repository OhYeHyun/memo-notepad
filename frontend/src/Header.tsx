import React from "react";
import { Me } from "./MemoScreen";

type Props = {
    me: Me | null;
    onLogout: () => Promise<void>;
    onOpenLogin: () => void;
    onOpenSignup: () => void;
    hideActions?: boolean;
};

export default function Header({ me, onLogout, onOpenLogin, onOpenSignup, hideActions }: Props) {
    if (hideActions) {
        return (
            <header className="topbar">
                <div className="brand brand--gradient">Notepad</div>
                <div className="spacer" />
            </header>
        );
    }

    const isGuest = me?.role === "ROLE_GUEST";
    const isAuthed = !!me && !isGuest;

    return (
        <header className="topbar">
            <div className="brand brand--gradient">Notepad</div>
            <div className="spacer" />
            {isAuthed ? (
                <div className="who">
                    <span className="pill">{me!.name}</span>
                    <button className="btn ghost" onClick={onLogout}>로그아웃</button>
                </div>
            ) : (
                <div className="cta-actions">
                    {onOpenLogin ? <button className="btn lg" onClick={onOpenLogin}>로그인</button> : <a className="btn lg" href="/">로그인</a>}
                    {onOpenSignup ? <button className="btn primary lg" onClick={onOpenSignup}>회원가입</button> : <a className="btn primary lg" href="/signup">회원가입</a>}
                </div>
            )}
        </header>
    );
}

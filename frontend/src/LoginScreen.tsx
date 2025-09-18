import React from "react";

type Props = {
    onGuest: () => Promise<void>;
    onLocalLoginClick: () => void;
};

export default function LoginScreen({ onGuest, onLocalLoginClick }: Props) {
    return (
        <div className="login-hero">
            <div className="login-card">
                <h1>Notepad</h1>
                <p>간단하고 빠른 개인 메모</p>

                <div className="btns">
                    <button className="btn ghost" onClick={onLocalLoginClick}>
                        아이디/비밀번호 로그인
                    </button>
                    <a className="btn primary" href="/oauth2/authorization/kakao">
                        카카오로 로그인
                    </a>
                    <button className="btn success" onClick={onGuest}>
                        게스트로 사용해보기
                    </button>
                </div>
                <small className="muted">게스트 메모는 일정 시간이 지나면 자동 삭제됩니다.</small>
            </div>
        </div>
    );
}
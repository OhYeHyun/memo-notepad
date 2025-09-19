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

                <div className="btns" style={{ justifyContent: "center" }}>
                    <button className="btn primary" onClick={onLocalLoginClick}>
                        시작하기
                    </button>
                    <button className="btn ghost" onClick={onGuest}>
                        게스트로 사용해보기
                    </button>
                </div>

                <small className="muted" style={{ display: "block", marginTop: 10 }}>
                    카카오 로그인·회원가입은 시작하기를 누른 뒤 모달에서 선택할 수 있어요.
                </small>
                <small className="muted">게스트 메모는 일정 시간이 지나면 자동 삭제됩니다.</small>
            </div>
        </div>
    );
}

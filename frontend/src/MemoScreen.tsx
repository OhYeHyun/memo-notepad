import React, {useEffect, useMemo, useRef, useState} from "react";
import { j, HttpError } from "./api";

export type Me = { name: string; role: string }; // ROLE_USER | ROLE_GUEST

type MemoDto = { memoId: number; content: string; isChecked: boolean }; // 🔹 서버 응답

type MemoItem = { id: number; content: string; isChecked: boolean };

const map = (m: MemoDto): MemoItem => ({ id: m.memoId, content: m.content, isChecked: m.isChecked });

type Props = {
    me: Me;
    onLogout: () => Promise<void>;
    onOpenLogin?: () => void;    // ← 게스트일 때 모달 열기(있으면 사용)
    onOpenSignup?: () => void;
};

export default function MemoScreen({ me, onLogout, onOpenLogin, onOpenSignup }: Props) {
    const [list, setList] = useState<MemoItem[]>([]);
    const [loading, setLoading] = useState(true);
    const [q, setQ] = useState("");
    const [dateQ, setDateQ] = useState("");
    const [newText, setNewText] = useState("");
    const [editing, setEditing] = useState<{ id: number; text: string } | null>(null);
    const [err, setErr] = useState<string | null>(null);

    const reqRef = useRef(0);

    const safeLoad = async () => {
        try {
            setErr(null);
            await load();
        } catch (e: any) {
            if (e?.message === "UNAUTHORIZED") {
                setErr("인증이 만료되었습니다. 다시 로그인해주세요.");
            } else {
                setErr(e?.message ?? "메모 목록 로드 실패");
            }
        }
    };

    // 로드
    const load = async () => {
        const myReq = ++reqRef.current;               // 이 호출의 id
        const qs = new URLSearchParams();
        if (q.trim()) qs.set("content", q.trim());
        if (dateQ) qs.set("createdAt", dateQ);
        const url = qs.toString() ? `/api/memos?${qs}` : "/api/memos";

        const data = await j<MemoDto[]>(url);
        if (reqRef.current === myReq) {               // 최신 응답만 적용
            setList(data.map(map));
        }
    };

    useEffect(() => {
        (async () => {
            setLoading(true);
            setErr(null);

            try {
                await load();
            } catch (e: any) {
                setErr(e.message ?? "메모 목록 로드 실패");
            } finally {
                setLoading(false);
            }
        })();
    }, []);

    const add = async () => {
        if (!newText.trim()) return;
        try {
            await j<MemoDto>("/api/memos", { method: "POST", body: JSON.stringify({ content: newText.trim() }) });
            setNewText("");
            await load(); // 🔹 서버 진실을 재조회
        } catch (e: any) {
            if (e instanceof HttpError && (e.status === 401 || e.status === 403)) {
                setErr("로그인이 필요합니다. 다시 로그인해 주세요.");
            } else {
                setErr("메모 추가 실패");
            }
        }
    };

    const remove = async (id: number) => {
        try {
            await j<void>(`/api/memos/${id}`, { method: "DELETE" });
            await load();
        } catch (e: any) {
            setErr("삭제 실패");
        }
    };

    const toggle = async (id: number) => {
        setList(prev => prev.map(m => (m.id === id ? { ...m, isChecked: !m.isChecked } : m)));
        try {
            await j<MemoDto>(`/api/memos/${id}/check`, { method: "PATCH" });
        } catch {
            setList(prev => prev.map(m => (m.id === id ? { ...m, isChecked: !m.isChecked } : m)));
            setErr("상태 변경 실패");
        }
    };

    const saveEdit = async () => {
        if (!editing || !editing.text.trim()) return;
        const { id, text } = editing;
        try {
            await j<MemoDto>(`/api/memos/${id}`, {
                method: "PATCH",
                body: JSON.stringify({ content: text.trim() }),
            });
            setEditing(null);
            await load();
        } catch (e: any) {
            setErr("수정 실패");
        }
    };

    const filteredInfo = useMemo(() => {
        const total = list.length;
        const done = list.filter((m) => m.isChecked).length;
        return { total, done };
    }, [list]);

    const submitSearch = async (e?: React.FormEvent) => {
        e?.preventDefault();
        try {
            setErr(null);
            await load();
        } catch {
            setErr("검색 실패");
        }
    };

    return (
        <div className="shell">
            <header className="topbar">
                <div className="brand">Notepad</div>
                <div className="spacer" />
                <div className="who">
                    <span className="pill">{me.role === "ROLE_GUEST" ? "GUEST" : me.name}</span>
                    <button className="btn ghost" onClick={onLogout}>로그아웃</button>
                </div>
            </header>

            {/* GUEST 안내/CTA */}
            {me.role === "ROLE_GUEST" && (
                <div className="cta">
                    <span>게스트 계정입니다. 저장은 임시로 보관돼요.</span>
                    <div className="cta-actions">
                        {onOpenLogin ? (
                            <button className="btn" onClick={onOpenLogin}>로그인</button>
                        ) : (
                            <a className="btn" href="/">{/* 로그인 화면 경로에 맞춰 수정 */}
                                로그인
                            </a>
                        )}
                        {onOpenSignup ? (
                            <button className="btn primary" onClick={onOpenSignup}>회원가입</button>
                        ) : (
                            <a className="btn primary" href="/login/signup">회원가입</a>
                        )}
                    </div>
                </div>
            )}

            <main className="content">
                {/* 검색 & 입력 */}
                <section className="panel">
                    <form className="searchbar" onSubmit={submitSearch}>
                        <div className="search-left">
                            <div className="field with-icon">
                                <input
                                    className="input"
                                    placeholder="내용 검색"
                                    value={q}
                                    onChange={(e) => setQ(e.target.value)}
                                />
                                <span className="icon">🔎</span>
                            </div>
                            <div className="field with-icon">
                                <input
                                    className="input"
                                    type="date"
                                    value={dateQ}
                                    onChange={(e) => setDateQ(e.target.value)}
                                />
                                <span className="icon">📅</span>
                            </div>
                        </div>
                        <div className="search-right">
                            <button className="btn" type="submit">검색</button>
                        </div>
                    </form>

                    <div className="row gap mt">
                        <input
                            className="input flex1"
                            placeholder={
                                me.role === "ROLE_GUEST"
                                    ? "메모를 입력하세요 (게스트는 임시 저장)"
                                    : "메모를 입력하세요"
                            }
                            value={newText}
                            onChange={(e) => setNewText(e.target.value)}
                            onKeyDown={(e) => e.key === "Enter" && add()}
                        />
                        <button className="btn primary" onClick={add}>
                            추가
                        </button>
                    </div>
                </section>

                {/* 리스트 */}
                <section className="panel">
                    <div className="row between">
                        <div className="muted">
                            총 {filteredInfo.total}개 • 완료 {filteredInfo.done}개
                        </div>
                    </div>

                    {loading ? (
                        <div className="muted mt">로딩 중…</div>
                    ) : err ? (
                        <div className="error mt">{err}</div>
                    ) : list.length === 0 ? (
                        <div className="muted mt">메모가 없습니다.</div>
                    ) : (
                        <ul className="list">
                            {list.map((m) => {
                                const isEditing = editing?.id === m.id;
                                return (
                                    <li key={m.id} className={`item ${m.isChecked ? "done" : ""}`}>
                                        <button
                                            className={`checkbox2 ${m.isChecked ? "on" : ""}`}
                                            onClick={() => toggle(m.id)}
                                            aria-pressed={m.isChecked}
                                            title="완료 토글"
                                        >
                                            <span className="box" />
                                            <svg className="tick" viewBox="0 0 24 24" aria-hidden>
                                                <path d="M6 12l4 4 8-8" />
                                            </svg>
                                        </button>

                                        {isEditing ? (
                                            <input
                                                className="input flex1"
                                                value={editing.text}
                                                onChange={(e)=> setEditing({id : m.id, text : e.target.value})}
                                                onKeyDown={(e)=> e.key === "Enter" && saveEdit()}
                                                autoFocus
                                            />
                                        ) : (
                                            <div className="text flex1">
                                                {m.content}
                                            </div>
                                        )}

                                        {isEditing ? (
                                            <button className="btn small primary" onClick={saveEdit}>저장</button>
                                        ) : (
                                            <button className="btn small" onClick={()=>setEditing({id:m.id, text:m.content})}>수정</button>
                                        )}
                                        <button className="btn small danger" onClick={()=>remove(m.id)}>삭제</button>
                                    </li>
                                );
                            })}
                        </ul>
                    )}
                </section>
            </main>
        </div>
    );
}

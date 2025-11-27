export class HttpError extends Error {
    status: number;
    constructor(status: number, message: string) { super(message); this.status = status; }
}

let onAuthRequired: (() => void) | null = null;

export async function j<T>(url: string, init?: RequestInit): Promise<T> {
    const res = await fetch(url, {
        credentials: "include",
        headers: { "Content-Type": "application/json" },
        ...init,
    });

    if (res.status === 204) return undefined as T;

    if (!res.ok) {
        if ((res.status === 401 || res.status === 403 || res.status == 440) && onAuthRequired) {
            onAuthRequired();
        }
        let msg = res.statusText;
        try {
            msg = await res.text();
        } catch {}
        throw new HttpError(res.status, msg || res.statusText);
    }

    const ct = res.headers.get("content-type") || "";
    if (!ct.includes("application/json")) return undefined as T;

    const text = await res.text();
    return (text ? JSON.parse(text) : undefined) as T;
}
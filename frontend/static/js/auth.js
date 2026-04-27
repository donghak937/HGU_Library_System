
async function fetchWithAuth(url, options = {}) {
    let accessToken = localStorage.getItem("accessToken");

    let response = await fetch(url, {
        ...options,
        headers: {
            ...options.headers,
            "Authorization": "Bearer " + accessToken
        }
    });

    if (response.status === 401) {
        const success = await refreshAccessToken();

        if (!success) {
            alert("세션이 만료되었습니다.");
            localStorage.clear();
            window.location.href = "/account/loginUI";
            return;
        }

        accessToken = localStorage.getItem("accessToken");

        response = await fetch(url, {
            ...options,
            headers: {
                ...options.headers,
                "Authorization": "Bearer " + accessToken
            }
        });
    }

    return response;
}

async function refreshAccessToken() {
    const refreshToken = localStorage.getItem("refreshToken");

    if (!refreshToken) return false;

    const res = await fetch("/account/refresh", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ refreshToken })
    });

    const data = await res.json();

    if (data.success) {
        localStorage.setItem("accessToken", data.accessToken);
        return true;
    }

    return false;
}
// ── Modal 控制 ──────────────────────────────────────────────────────────────
function openModal(id) {
    var el = document.getElementById(id);
    if (el) el.classList.add('open');
}
function closeModal(id) {
    var el = document.getElementById(id);
    if (el) {
        el.classList.remove('open');
        // 清空表單
        var form = el.querySelector('form');
        if (form) form.reset();
        // 清除隱藏的 id
        var hiddenId = el.querySelector('input[name="id"]');
        if (hiddenId) hiddenId.value = '';
    }
}

// 點擊背景關閉
document.addEventListener('click', function(e) {
    if (e.target.classList.contains('modal-overlay')) {
        e.target.classList.remove('open');
    }
});

// ── Toast 通知 ──────────────────────────────────────────────────────────────
function showToast(msg, type) {
    var icons = { success: '✅', error: '❌', info: 'ℹ️' };
    var t = document.getElementById('toast');
    if (!t) {
        t = document.createElement('div');
        t.id = 'toast';
        t.className = 'toast';
        document.body.appendChild(t);
    }
    t.className = 'toast ' + (type || 'success');
    t.innerHTML = '<span>' + (icons[type] || '✅') + '</span><span>' + msg + '</span>';
    t.classList.add('show');
    setTimeout(function() { t.classList.remove('show'); }, 3000);
}

// ── AJAX 送出表單 ───────────────────────────────────────────────────────────
function submitForm(formId, url, method, successMsg, modalId) {
    var form = document.getElementById(formId);
    if (!form) return;

    var data = {};
    var inputs = form.querySelectorAll('input, select, textarea');
    inputs.forEach(function(input) {
        if (input.name) data[input.name] = input.value;
    });

    fetch(url, {
        method: method || 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    })
    .then(function(res) {
        if (res.ok) {
            showToast(successMsg || '操作成功', 'success');
            if (modalId) closeModal(modalId);
            setTimeout(function() { location.reload(); }, 800);
        } else {
            return res.text().then(function(t) { throw new Error(t); });
        }
    })
    .catch(function(err) {
        showToast('操作失敗：' + err.message, 'error');
    });
}

// ── 刪除確認 ────────────────────────────────────────────────────────────────
function confirmDelete(url, name) {
    var modal = document.getElementById('deleteModal');
    if (!modal) return;
    document.getElementById('deleteTargetName').textContent = name || '此項目';
    document.getElementById('deleteConfirmBtn').onclick = function() {
        fetch(url, { method: 'DELETE' })
        .then(function(res) {
            if (res.ok) {
                showToast('已成功刪除', 'success');
                closeModal('deleteModal');
                setTimeout(function() { location.reload(); }, 800);
            } else {
                showToast('刪除失敗', 'error');
            }
        })
        .catch(function() { showToast('刪除失敗', 'error'); });
    };
    openModal('deleteModal');
}

// ── 編輯：填入現有資料 ──────────────────────────────────────────────────────
function fillEditForm(modalId, data) {
    var modal = document.getElementById(modalId);
    if (!modal) return;
    Object.keys(data).forEach(function(key) {
        var el = modal.querySelector('[name="' + key + '"]');
        if (el) el.value = data[key];
    });
    openModal(modalId);
}

/**
 * 安全查询系统前端加密逻辑
 */
document.addEventListener('DOMContentLoaded', function() {
    // 当前会话密钥和IV
    let sessionKey = null;
    let iv = null;
    let currentUserId = null;
    
    // 初始化表单提交事件
    document.querySelectorAll('.query-form').forEach(form => {
        form.addEventListener('submit', async function(e) {
            e.preventDefault();
            
            const formData = new FormData(this);
            const userId = formData.get('userId');
            const idNumber = formData.get('idNumber');
            
            if (!idNumber || idNumber.length !== 18) {
                showError('请输入有效的18位身份证号码');
                return;
            }
            
            try {
                showLoading(true);
                
                // 如果用户ID变化或会话密钥不存在，重新建立会话
                if (currentUserId !== userId || !sessionKey) {
                    await establishSession(userId);
                    currentUserId = userId;
                }
                
                // 加密身份证号并发送查询请求
                await sendQuery(idNumber, userId);
                
            } catch (error) {
                console.error('查询过程中发生错误:', error);
                showError('查询过程中发生错误: ' + error.message);
            } finally {
                showLoading(false);
            }
        });
    });
    
    /**
     * 建立安全会话
     * @param {string} userId 用户ID
     */
    async function establishSession(userId) {
        try {
            // 1. 获取用户RSA公钥
            const publicKeyResponse = await fetch(`/api/key/${userId}`);
            if (!publicKeyResponse.ok) {
                throw new Error('获取公钥失败');
            }
            const publicKey = await publicKeyResponse.text();
            
            // 2. 生成AES会话密钥
            const aesKey = CryptoJS.lib.WordArray.random(32); // 256位AES密钥
            sessionKey = CryptoJS.enc.Base64.stringify(aesKey);
            
            // 3. 生成随机IV
            const ivArray = CryptoJS.lib.WordArray.random(16); // 16字节IV (CBC模式)
            iv = CryptoJS.enc.Base64.stringify(ivArray);
            
            // 4. 使用RSA公钥加密会话密钥
            const jsEncrypt = new JSEncrypt();
            jsEncrypt.setPublicKey(publicKey);
            const encryptedSessionKey = jsEncrypt.encrypt(sessionKey);
            
            // 5. 发送加密的会话密钥和IV到后端
            const sessionResponse = await fetch('/api/session', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    userId: userId,
                    encryptedSessionKey: encryptedSessionKey,
                    iv: iv
                })
            });
            
            if (!sessionResponse.ok) {
                throw new Error('建立会话失败');
            }
            
            console.log('会话建立成功');
        } catch (error) {
            console.error('建立会话失败:', error);
            sessionKey = null;
            iv = null;
            throw new Error('建立安全会话失败: ' + error.message);
        }
    }
    
    /**
     * 发送加密查询请求
     * @param {string} idNumber 身份证号
     * @param {string} userId 用户ID
     */
    async function sendQuery(idNumber, userId) {
        try {
            // 1. 使用AES-CBC加密身份证号
            const key = CryptoJS.enc.Base64.parse(sessionKey);
            const ivParam = CryptoJS.enc.Base64.parse(iv);
            
            // 使用明确的CBC模式和PKCS7填充
            const encrypted = CryptoJS.AES.encrypt(idNumber, key, {
                iv: ivParam,
                mode: CryptoJS.mode.CBC,
                padding: CryptoJS.pad.Pkcs7
            });
            
            const encryptedIdNumber = encrypted.toString();
            
            // 2. 发送加密的身份证号到后端
            const queryResponse = await fetch('/api/query', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    userId: userId,
                    encryptedData: encryptedIdNumber,
                    iv: iv
                })
            });
            
            const result = await queryResponse.json();
            
            if (result.success) {
                // 3. 解密返回的数据
                const encryptedResult = result.encryptedData;
                const decrypted = decryptResult(encryptedResult);
                displayResult(decrypted);
            } else {
                showError(result.message || '查询失败');
            }
        } catch (error) {
            console.error('查询失败:', error);
            throw new Error('查询失败: ' + error.message);
        }
    }
    
    /**
     * 解密查询结果
     * @param {string} encryptedData 加密的数据
     * @returns {object} 解密后的数据对象
     */
    function decryptResult(encryptedData) {
        try {
            const key = CryptoJS.enc.Base64.parse(sessionKey);
            const ivParam = CryptoJS.enc.Base64.parse(iv);
            
            // 使用明确的CBC模式和PKCS7填充
            const decrypted = CryptoJS.AES.decrypt(encryptedData, key, {
                iv: ivParam,
                mode: CryptoJS.mode.CBC,
                padding: CryptoJS.pad.Pkcs7
            });
            
            const decryptedText = decrypted.toString(CryptoJS.enc.Utf8);
            return JSON.parse(decryptedText);
        } catch (error) {
            console.error('解密结果失败:', error);
            throw new Error('解密结果失败');
        }
    }
    
    /**
     * 显示查询结果
     * @param {object} data 解密后的数据
     */
    function displayResult(data) {
        const resultContainer = document.getElementById('resultContainer');
        const resultContent = document.getElementById('resultContent');
        
        // 数据脱敏处理
        if (data.phone) {
            data.phone = maskPhone(data.phone);
        }
        if (data.email) {
            data.email = maskEmail(data.email);
        }
        
        // 构建结果HTML
        let html = '<table class="table">';
        html += '<tr><th>姓名</th><td>' + (data.name || '-') + '</td></tr>';
        html += '<tr><th>地址</th><td>' + (data.address || '-') + '</td></tr>';
        html += '<tr><th>电话</th><td>' + (data.phone || '-') + '</td></tr>';
        html += '<tr><th>邮箱</th><td>' + (data.email || '-') + '</td></tr>';
        html += '</table>';
        
        resultContent.innerHTML = html;
        resultContainer.style.display = 'block';
    }
    
    /**
     * 显示错误信息
     * @param {string} message 错误信息
     */
    function showError(message) {
        const resultContainer = document.getElementById('resultContainer');
        const resultContent = document.getElementById('resultContent');
        
        resultContent.innerHTML = '<div class="alert alert-danger">' + message + '</div>';
        resultContainer.style.display = 'block';
    }
    
    /**
     * 显示/隐藏加载状态
     * @param {boolean} show 是否显示
     */
    function showLoading(show) {
        document.getElementById('loading').style.display = show ? 'block' : 'none';
        if (show) {
            document.getElementById('resultContainer').style.display = 'none';
        }
    }
    
    /**
     * 手机号脱敏
     * @param {string} phone 手机号
     * @returns {string} 脱敏后的手机号
     */
    function maskPhone(phone) {
        if (!phone || phone.length < 8) return phone;
        return phone.substring(0, 3) + '****' + phone.substring(phone.length - 4);
    }
    
    /**
     * 邮箱脱敏
     * @param {string} email 邮箱
     * @returns {string} 脱敏后的邮箱
     */
    function maskEmail(email) {
        if (!email || email.indexOf('@') === -1) return email;
        const parts = email.split('@');
        let username = parts[0];
        if (username.length > 2) {
            username = username.substring(0, 1) + '***' + username.substring(username.length - 1);
        }
        return username + '@' + parts[1];
    }
});
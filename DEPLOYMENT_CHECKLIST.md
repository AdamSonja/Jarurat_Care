# Deployment Checklist for WhatsApp Business API

## ✅ Pre-Deployment Checklist
- [ ] Meta Developer account created
- [ ] WhatsApp Business app created
- [ ] Phone number verified in WhatsApp Business
- [ ] Access token generated
- [ ] Phone Number ID obtained
- [ ] Webhook verify token created

## ✅ Render Deployment Checklist
- [ ] Application deployed on Render
- [ ] Environment variables set in Render dashboard
- [ ] Application is running (check Render logs)
- [ ] Health check endpoint accessible

## ✅ WhatsApp Configuration Checklist
- [ ] Webhook URL configured in Meta Dashboard
- [ ] Webhook verification successful
- [ ] Webhook events subscribed (messages, deliveries, reads)
- [ ] Message templates created and approved (if needed)

## ✅ Testing Checklist
- [ ] Health check endpoint returns 200 OK
- [ ] Webhook verification endpoint works
- [ ] Message sending API works
- [ ] Message retrieval API works
- [ ] Webhook receives incoming messages

## 🔧 Quick Test Commands

### 1. Health Check
```bash
curl https://your-app-name.onrender.com/health
```

### 2. Webhook Verification
```bash
curl "https://your-app-name.onrender.com/webhook?hub.mode=subscribe&hub.verify_token=YOUR_TOKEN&hub.challenge=test"
```

### 3. Send Test Message
```bash
curl -X POST https://your-app-name.onrender.com/send \
  -H "Content-Type: application/json" \
  -d '{"to": "1234567890", "text": "Test message"}'
```

### 4. Get Messages
```bash
curl https://your-app-name.onrender.com/messages/1234567890
```

## 🚨 Common Issues & Solutions

### Issue: Health check fails
**Check:**
- Render service is running
- Environment variables are set
- Application logs for errors

### Issue: Webhook verification fails
**Check:**
- Webhook URL is correct
- Verify token matches exactly
- Service is accessible from internet

### Issue: Messages not sending
**Check:**
- Access token is valid
- Phone Number ID is correct
- Phone number is verified in WhatsApp Business

### Issue: Environment variables not loading
**Check:**
- Variable names match exactly
- No extra spaces in values
- Redeploy after setting variables

## 📞 Support Contacts
- **Render Support**: [support.render.com](https://support.render.com)
- **Meta Developer Support**: [developers.facebook.com/support](https://developers.facebook.com/support)
- **WhatsApp Business API Docs**: [developers.facebook.com/docs/whatsapp](https://developers.facebook.com/docs/whatsapp)

## 🎯 Next Steps After Checklist Completion
1. Implement your business logic
2. Add authentication to API endpoints
3. Set up monitoring and alerts
4. Create message templates for your business
5. Test with real users
6. Monitor performance and scale as needed

---
**Status:** ⏳ In Progress | ✅ Complete | ❌ Needs Attention 
# WhatsApp Business API Setup Guide

## Prerequisites
- Facebook account
- Business phone number (can be your existing number)
- Domain with HTTPS for webhooks (for production)

## Step-by-Step Setup

### 1. Create Meta Developer Account
1. Go to [Meta for Developers](https://developers.facebook.com/)
2. Sign in with your Facebook account
3. Accept the developer terms

### 2. Create a Meta App
1. Click "Create App" in the developer dashboard
2. Select "Business" as the app type
3. Fill in your app details:
   - App Name: Your business name
   - App Contact Email: Your email
   - Business Account: Select or create one

### 3. Add WhatsApp Product
1. In your app dashboard, click "Add Product"
2. Find "WhatsApp" in the product catalog
3. Click "Set Up" on WhatsApp
4. This will give you access to the WhatsApp Business API

### 4. Configure WhatsApp Business API

#### A. Get Your Phone Number ID
1. Go to "WhatsApp" → "Getting Started" in your app
2. Click "Add phone number"
3. Enter your business phone number
4. Verify the number via SMS/call
5. Note down the **Phone Number ID** (long numeric string)

#### B. Generate Access Token
1. In "WhatsApp" → "Getting Started"
2. You'll see a "Temporary access token" (valid for 24 hours)
3. For production, create a permanent token:
   - Go to "System Users" in app settings
   - Create a system user with "WhatsApp Business Manager" role
   - Generate a token for that system user

#### C. Set Up Webhook
1. In "WhatsApp" → "Configuration"
2. Set your webhook URL: `https://yourdomain.com/api/whatsapp/webhook`
3. Create a custom **Verify Token** (any secure string you choose)
4. Subscribe to these webhook events:
   - `messages`
   - `message_deliveries`
   - `message_reads`

### 5. Environment Variables Setup

Create a `.env` file or set these environment variables:

```bash
# WhatsApp API Credentials
WHATSAPP_ACCESS_TOKEN=your_permanent_access_token_here
WHATSAPP_PHONE_NUMBER_ID=your_phone_number_id_here
WHATSAPP_WEBHOOK_VERIFY_TOKEN=your_custom_webhook_verify_token_here
WHATSAPP_API_URL=https://graph.facebook.com/v19.0
WHATSAPP_APP_ID=your_app_id_here
WHATSAPP_APP_SECRET=your_app_secret_here

# Firebase Configuration
FIREBASE_CONFIG_PATH=path/to/your/firebase-service-account.json
```

### 6. Testing Your Setup

#### Test Message Sending
```bash
curl -X POST http://localhost:8081/api/whatsapp/send \
  -H "Content-Type: application/json" \
  -d '{
    "to": "1234567890",
    "text": "Hello from JaruratCare!"
  }'
```

#### Test Webhook Verification
```bash
curl "http://localhost:8081/api/whatsapp/webhook?hub.mode=subscribe&hub.verify_token=your_webhook_verify_token&hub.challenge=test_challenge"
```

## Important Notes

### Security
- Never commit API keys to version control
- Use environment variables for all sensitive data
- Rotate access tokens regularly
- Use HTTPS in production

### Rate Limits
- WhatsApp Business API has rate limits
- Standard tier: 1,000 messages per second
- Monitor your usage in the Meta Developer Dashboard

### Message Templates
- For non-session messages, you need pre-approved templates
- Templates must be approved by Meta before use
- Session messages (replies within 24 hours) don't need templates

### Webhook Requirements
- Must be HTTPS in production
- Must respond within 20 seconds
- Must return HTTP 200 for successful processing

## Troubleshooting

### Common Issues
1. **401 Unauthorized**: Check your access token
2. **404 Not Found**: Verify phone number ID
3. **Webhook verification fails**: Check verify token
4. **Message not delivered**: Ensure phone number is verified

### Support Resources
- [WhatsApp Business API Documentation](https://developers.facebook.com/docs/whatsapp)
- [Meta Developer Community](https://developers.facebook.com/community/)
- [WhatsApp Business API Status](https://developers.facebook.com/status/)

## Next Steps
1. Set up message templates for your business
2. Implement message status tracking
3. Add media message support
4. Set up automated responses
5. Implement conversation management 
{
  <#include "abstracto_color">,
  <#include "member_author">
  <#assign member=postingMember>
  <@member_author member=member/>,
  <#include "full_member_info">
  "description": "<@safe_include "referralListener_referral_post_description"/>",
  "fields": [
  <#list referrals as referral><#assign referral=referral>
      {
        "name": "<@safe_include "referral_link_type_${referral.type.key}"/>",
        "value": "[${referral.referralIdentifier?json_string}](${referral.referralLink?json_string})"
      }
      <#sep>,
      </#list>
  ]
}
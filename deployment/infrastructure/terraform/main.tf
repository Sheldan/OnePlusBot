module "hetzner" {
  source            = "git@github.com:Sheldan/discord-bot-terraform-module.git//hetzner-bot?ref=v1.0.0"
  ssh_key_key       = "Sheldan"
  project_name      = "OnePlusBot"
  user_name         = "sheldan"
  hcloud_token      = var.hcloud_token
}
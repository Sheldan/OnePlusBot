terraform {
  cloud {
    hostname = "app.terraform.io"
    organization = "OnePlusBot"

    workspaces {
      name = "main"
    }
  }
}
export type Json =
  | string
  | number
  | boolean
  | null
  | { [key: string]: Json | undefined }
  | Json[]

export type Database = {
  // Allows to automatically instantiate createClient with right options
  // instead of createClient<Database, { PostgrestVersion: 'XX' }>(URL, KEY)
  __InternalSupabase: {
    PostgrestVersion: "14.18"
  }
  public: {
    Tables: {
      account_phones: {
        Row: {
          phone: string
          user_id: string
        }
        Insert: {
          phone: string
          user_id: string
        }
        Update: {
          phone?: string
          user_id?: string
        }
        Relationships: []
      }
      addresses: {
        Row: {
          full_address: string
          id: string
          locality: string
          user_id: string
        }
        Insert: {
          full_address: string
          id?: string
          locality: string
          user_id: string
        }
        Update: {
          full_address?: string
          id?: string
          locality?: string
          user_id?: string
        }
        Relationships: []
      }
      bookings: {
        Row: {
          address_id: string | null
          amount: number
          created_at: string
          customer_id: string
          id: string
          notes: string | null
          package_id: string
          payment_mode: Database["public"]["Enums"]["payment_mode"]
          provider_id: string | null
          slot_time: string
          status: Database["public"]["Enums"]["booking_status"]
        }
        Insert: {
          address_id?: string | null
          amount?: number
          created_at?: string
          customer_id: string
          id?: string
          notes?: string | null
          package_id: string
          payment_mode?: Database["public"]["Enums"]["payment_mode"]
          provider_id?: string | null
          slot_time: string
          status?: Database["public"]["Enums"]["booking_status"]
        }
        Update: {
          address_id?: string | null
          amount?: number
          created_at?: string
          customer_id?: string
          id?: string
          notes?: string | null
          package_id?: string
          payment_mode?: Database["public"]["Enums"]["payment_mode"]
          provider_id?: string | null
          slot_time?: string
          status?: Database["public"]["Enums"]["booking_status"]
        }
        Relationships: [
          {
            foreignKeyName: "bookings_address_id_fkey"
            columns: ["address_id"]
            isOneToOne: false
            referencedRelation: "addresses"
            referencedColumns: ["id"]
          },
          {
            foreignKeyName: "bookings_package_id_fkey"
            columns: ["package_id"]
            isOneToOne: false
            referencedRelation: "packages"
            referencedColumns: ["id"]
          },
          {
            foreignKeyName: "bookings_provider_id_fkey"
            columns: ["provider_id"]
            isOneToOne: false
            referencedRelation: "providers"
            referencedColumns: ["id"]
          },
        ]
      }
      complaints: {
        Row: {
          booking_id: string | null
          created_at: string
          id: string
          message: string
          status: string
          user_id: string
        }
        Insert: {
          booking_id?: string | null
          created_at?: string
          id?: string
          message: string
          status?: string
          user_id: string
        }
        Update: {
          booking_id?: string | null
          created_at?: string
          id?: string
          message?: string
          status?: string
          user_id?: string
        }
        Relationships: [
          {
            foreignKeyName: "complaints_booking_id_fkey"
            columns: ["booking_id"]
            isOneToOne: false
            referencedRelation: "bookings"
            referencedColumns: ["id"]
          },
        ]
      }
      offers: {
        Row: {
          code: string
          created_at: string
          details: string
          details_hi: string
          ends_at: string | null
          id: string
          is_active: boolean
          service_id: string | null
          sort: number
          starts_at: string
          terms: string
          terms_hi: string
          title: string
          title_hi: string
        }
        Insert: {
          code?: string
          created_at?: string
          details: string
          details_hi?: string
          ends_at?: string | null
          id?: string
          is_active?: boolean
          service_id?: string | null
          sort?: number
          starts_at?: string
          terms?: string
          terms_hi?: string
          title: string
          title_hi?: string
        }
        Update: {
          code?: string
          created_at?: string
          details?: string
          details_hi?: string
          ends_at?: string | null
          id?: string
          is_active?: boolean
          service_id?: string | null
          sort?: number
          starts_at?: string
          terms?: string
          terms_hi?: string
          title?: string
          title_hi?: string
        }
        Relationships: [
          {
            foreignKeyName: "offers_service_id_fkey"
            columns: ["service_id"]
            isOneToOne: false
            referencedRelation: "services"
            referencedColumns: ["id"]
          },
        ]
      }
      packages: {
        Row: {
          duration_minutes: number
          id: string
          name: string
          price: number
          service_id: string
        }
        Insert: {
          duration_minutes: number
          id?: string
          name: string
          price: number
          service_id: string
        }
        Update: {
          duration_minutes?: number
          id?: string
          name?: string
          price?: number
          service_id?: string
        }
        Relationships: [
          {
            foreignKeyName: "packages_service_id_fkey"
            columns: ["service_id"]
            isOneToOne: false
            referencedRelation: "services"
            referencedColumns: ["id"]
          },
        ]
      }
      profiles: {
        Row: {
          created_at: string
          full_name: string | null
          id: string
          language: string
          phone: string | null
          registration_completed: boolean
        }
        Insert: {
          created_at?: string
          full_name?: string | null
          id: string
          language?: string
          phone?: string | null
          registration_completed?: boolean
        }
        Update: {
          created_at?: string
          full_name?: string | null
          id?: string
          language?: string
          phone?: string | null
          registration_completed?: boolean
        }
        Relationships: []
      }
      providers: {
        Row: {
          areas: string[]
          bio: string
          business_address: string
          created_at: string
          experience_years: number
          full_name: string | null
          id: string
          id_document_url: string | null
          is_online: boolean
          is_verified: boolean
          jobs_completed: number
          phone: string | null
          rating: number
          services: string[]
          status: Database["public"]["Enums"]["provider_status"]
        }
        Insert: {
          areas?: string[]
          bio?: string
          business_address?: string
          created_at?: string
          experience_years?: number
          full_name?: string | null
          id: string
          id_document_url?: string | null
          is_online?: boolean
          is_verified?: boolean
          jobs_completed?: number
          phone?: string | null
          rating?: number
          services?: string[]
          status?: Database["public"]["Enums"]["provider_status"]
        }
        Update: {
          areas?: string[]
          bio?: string
          business_address?: string
          created_at?: string
          experience_years?: number
          full_name?: string | null
          id?: string
          id_document_url?: string | null
          is_online?: boolean
          is_verified?: boolean
          jobs_completed?: number
          phone?: string | null
          rating?: number
          services?: string[]
          status?: Database["public"]["Enums"]["provider_status"]
        }
        Relationships: []
      }
      reviews: {
        Row: {
          booking_id: string
          comment: string | null
          created_at: string
          customer_id: string
          id: string
          provider_id: string | null
          rating: number
        }
        Insert: {
          booking_id: string
          comment?: string | null
          created_at?: string
          customer_id: string
          id?: string
          provider_id?: string | null
          rating: number
        }
        Update: {
          booking_id?: string
          comment?: string | null
          created_at?: string
          customer_id?: string
          id?: string
          provider_id?: string | null
          rating?: number
        }
        Relationships: [
          {
            foreignKeyName: "reviews_booking_id_fkey"
            columns: ["booking_id"]
            isOneToOne: false
            referencedRelation: "bookings"
            referencedColumns: ["id"]
          },
        ]
      }
      services: {
        Row: {
          icon: string
          id: string
          is_active: boolean
          name: string
          name_hi: string | null
          sort: number
        }
        Insert: {
          icon: string
          id?: string
          is_active?: boolean
          name: string
          name_hi?: string | null
          sort?: number
        }
        Update: {
          icon?: string
          id?: string
          is_active?: boolean
          name?: string
          name_hi?: string | null
          sort?: number
        }
        Relationships: []
      }
      user_roles: {
        Row: {
          id: string
          role: Database["public"]["Enums"]["app_role"]
          user_id: string
        }
        Insert: {
          id?: string
          role: Database["public"]["Enums"]["app_role"]
          user_id: string
        }
        Update: {
          id?: string
          role?: Database["public"]["Enums"]["app_role"]
          user_id?: string
        }
        Relationships: []
      }
    }
    Views: {
      [_ in never]: never
    }
    Functions: {
      choose_role: {
        Args: { _role: Database["public"]["Enums"]["app_role"] }
        Returns: undefined
      }
      complete_registration: {
        Args: {
          _address: string
          _areas: string[]
          _bio: string
          _experience: number
          _language: string
          _locality: string
          _name: string
          _phone: string
          _role: Database["public"]["Enums"]["app_role"]
          _services: string[]
          _user_id: string
        }
        Returns: undefined
      }
      has_role: {
        Args: {
          _role: Database["public"]["Enums"]["app_role"]
          _user_id: string
        }
        Returns: boolean
      }
    }
    Enums: {
      app_role: "admin" | "customer" | "provider"
      booking_status:
        | "pending"
        | "assigned"
        | "accepted"
        | "on_the_way"
        | "started"
        | "completed"
        | "cancelled"
      payment_mode: "cash" | "online"
      provider_status: "pending" | "approved" | "blocked"
    }
    CompositeTypes: {
      [_ in never]: never
    }
  }
}

type DatabaseWithoutInternals = Omit<Database, "__InternalSupabase">

type DefaultSchema = DatabaseWithoutInternals[Extract<keyof Database, "public">]

export type Tables<
  DefaultSchemaTableNameOrOptions extends
    | keyof (DefaultSchema["Tables"] & DefaultSchema["Views"])
    | { schema: keyof DatabaseWithoutInternals },
  TableName extends (DefaultSchemaTableNameOrOptions extends {
    schema: keyof DatabaseWithoutInternals
  }
    ? keyof (DatabaseWithoutInternals[DefaultSchemaTableNameOrOptions["schema"]]["Tables"] &
        DatabaseWithoutInternals[DefaultSchemaTableNameOrOptions["schema"]]["Views"])
    : never) = never,
> = DefaultSchemaTableNameOrOptions extends {
  schema: keyof DatabaseWithoutInternals
}
  ? (DatabaseWithoutInternals[DefaultSchemaTableNameOrOptions["schema"]]["Tables"] &
      DatabaseWithoutInternals[DefaultSchemaTableNameOrOptions["schema"]]["Views"])[TableName] extends {
      Row: infer R
    }
    ? R
    : never
  : DefaultSchemaTableNameOrOptions extends keyof (DefaultSchema["Tables"] &
        DefaultSchema["Views"])
    ? (DefaultSchema["Tables"] &
        DefaultSchema["Views"])[DefaultSchemaTableNameOrOptions] extends {
        Row: infer R
      }
      ? R
      : never
    : never

export type TablesInsert<
  DefaultSchemaTableNameOrOptions extends
    | keyof DefaultSchema["Tables"]
    | { schema: keyof DatabaseWithoutInternals },
  TableName extends (DefaultSchemaTableNameOrOptions extends {
    schema: keyof DatabaseWithoutInternals
  }
    ? keyof DatabaseWithoutInternals[DefaultSchemaTableNameOrOptions["schema"]]["Tables"]
    : never) = never,
> = DefaultSchemaTableNameOrOptions extends {
  schema: keyof DatabaseWithoutInternals
}
  ? DatabaseWithoutInternals[DefaultSchemaTableNameOrOptions["schema"]]["Tables"][TableName] extends {
      Insert: infer I
    }
    ? I
    : never
  : DefaultSchemaTableNameOrOptions extends keyof DefaultSchema["Tables"]
    ? DefaultSchema["Tables"][DefaultSchemaTableNameOrOptions] extends {
        Insert: infer I
      }
      ? I
      : never
    : never

export type TablesUpdate<
  DefaultSchemaTableNameOrOptions extends
    | keyof DefaultSchema["Tables"]
    | { schema: keyof DatabaseWithoutInternals },
  TableName extends (DefaultSchemaTableNameOrOptions extends {
    schema: keyof DatabaseWithoutInternals
  }
    ? keyof DatabaseWithoutInternals[DefaultSchemaTableNameOrOptions["schema"]]["Tables"]
    : never) = never,
> = DefaultSchemaTableNameOrOptions extends {
  schema: keyof DatabaseWithoutInternals
}
  ? DatabaseWithoutInternals[DefaultSchemaTableNameOrOptions["schema"]]["Tables"][TableName] extends {
      Update: infer U
    }
    ? U
    : never
  : DefaultSchemaTableNameOrOptions extends keyof DefaultSchema["Tables"]
    ? DefaultSchema["Tables"][DefaultSchemaTableNameOrOptions] extends {
        Update: infer U
      }
      ? U
      : never
    : never

export type Enums<
  DefaultSchemaEnumNameOrOptions extends
    | keyof DefaultSchema["Enums"]
    | { schema: keyof DatabaseWithoutInternals },
  EnumName extends (DefaultSchemaEnumNameOrOptions extends {
    schema: keyof DatabaseWithoutInternals
  }
    ? keyof DatabaseWithoutInternals[DefaultSchemaEnumNameOrOptions["schema"]]["Enums"]
    : never) = never,
> = DefaultSchemaEnumNameOrOptions extends {
  schema: keyof DatabaseWithoutInternals
}
  ? DatabaseWithoutInternals[DefaultSchemaEnumNameOrOptions["schema"]]["Enums"][EnumName]
  : DefaultSchemaEnumNameOrOptions extends keyof DefaultSchema["Enums"]
    ? DefaultSchema["Enums"][DefaultSchemaEnumNameOrOptions]
    : never

export type CompositeTypes<
  PublicCompositeTypeNameOrOptions extends
    | keyof DefaultSchema["CompositeTypes"]
    | { schema: keyof DatabaseWithoutInternals },
  CompositeTypeName extends (PublicCompositeTypeNameOrOptions extends {
    schema: keyof DatabaseWithoutInternals
  }
    ? keyof DatabaseWithoutInternals[PublicCompositeTypeNameOrOptions["schema"]]["CompositeTypes"]
    : never) = never,
> = PublicCompositeTypeNameOrOptions extends {
  schema: keyof DatabaseWithoutInternals
}
  ? DatabaseWithoutInternals[PublicCompositeTypeNameOrOptions["schema"]]["CompositeTypes"][CompositeTypeName]
  : PublicCompositeTypeNameOrOptions extends keyof DefaultSchema["CompositeTypes"]
    ? DefaultSchema["CompositeTypes"][PublicCompositeTypeNameOrOptions]
    : never

export const Constants = {
  public: {
    Enums: {
      app_role: ["admin", "customer", "provider"],
      booking_status: [
        "pending",
        "assigned",
        "accepted",
        "on_the_way",
        "started",
        "completed",
        "cancelled",
      ],
      payment_mode: ["cash", "online"],
      provider_status: ["pending", "approved", "blocked"],
    },
  },
} as const

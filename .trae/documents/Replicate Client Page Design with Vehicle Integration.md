I will replicate the client list page exactly as shown in the image, implementing the dark card layout with nested vehicle cards and the "Add Vehicle" button with the specific blue border hover effect.

1. **Update Domain Models**:

   * **`Cliente`**: Add `email`, `dataCadastro` (LocalDate), and `@OneToMany` relationship with `Veiculo`.

   * **`Veiculo`**: Add `cor` and `ano` fields to match the vehicle card details ("Color • Year").

2. **Update Controller & Service Logic**:

   * **`ClienteService`**: Ensure `dataCadastro` is set to `LocalDate.now()` on creation.

   * **`VeiculoController`**: Modify the `novo` method to accept an optional `clienteId` parameter, allowing the "Add Vehicle" button to pre-select the client.

3. **Frontend Implementation (`cliente/list.html`)**:

   * Replace the existing `<table>` with a `<div>` based grid layout.

   * Implement the **Client Card** structure:

     * Header: Name, "Client since \[date]".

     * Contact Info: Phone and Email with icons.

     * **Vehicle Grid**: A nested grid displaying each vehicle card.

   * Implement the **"Add Vehicle" Button**:

     * Create a specific CSS class for the dashed border button.

     * Add the `:hover` state with `border-color: var(--primary-color)` (blue) as requested.

     * Link it to `/veiculos/novo?clienteId={id}`.

4. **CSS Updates (`style.css`)**:

   * Add styles for `.client-card`, `.vehicle-card`, and the `.btn-add-vehicle` with the specific hover effect.

   * Ensure typography and spacing match the "AutoShine" dark theme.

5. **Verification**:

   * I will verify the page structure and the hover effect on the "Add Vehicle" button.

   * I will ensure the data flow (Client -> Vehicles) works correctly in the view.

